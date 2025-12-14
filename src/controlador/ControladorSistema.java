package controlador;

import modelo.Cliente;
import excepcion.BancoException;
import modelo.Contrato;
import modelo.Cuenta;
import modelo.Rut;
import modelo.Tarjeta;
import persistencia.IOPersistencia;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

public class ControladorSistema implements Serializable {

    private static ControladorSistema instancia;

    private ControladorSistema() {
    }

    public static ControladorSistema getInstancia() {
        if (instancia == null) {
            instancia = new ControladorSistema();
        }
        return instancia;
    }

    // Se inician metodos del sistema!
    ArrayList<Cliente> clientes = new ArrayList<>();

    public void crearCliente(String nombre, String rut, String domicilio, int edad, double ingreso, double gasto, boolean castigado) throws BancoException {

        Rut rutObjeto = Rut.of(rut);

        if (rutObjeto == null) {
            // Si el formato es inválido, lanzamos una excepción.
            // La vista capturará esta excepción y mostrará el error.
            throw new BancoException("El formato del RUT ingresado es incorrecto o inválido. Use el formato XX.XXX.XXX-X.");
        }


        if (findCliente(rut).isPresent() || findClientePorNombre(nombre).isPresent()) {
            throw new BancoException("Cliente ya existe");
        }

        Cliente cliente = new Cliente(nombre, rut, domicilio, edad, ingreso, gasto, castigado);
        clientes.add(cliente);

    }

    public void generarContratoCliente(String clienteRut, String tipoCuenta) throws BancoException {

        Optional<Cliente> cliente = findCliente(clienteRut);

        if (cliente.isPresent()) {

            Contrato contrato = new Contrato(tipoCuenta);

            if (tipoCuenta.equalsIgnoreCase("LINEA CREDITO") && cliente.get().getRatioEndeudamiento() == -1) {
                throw new BancoException("No se puede generar contrato de línea de crédito para clientes rechazados");
            }

            try{
                cliente.get().agregarContrato(contrato);
            } catch (BancoException e) {
                throw new BancoException(e.getMessage());
            }
        }
    }

    public boolean firmarContratoCliente(String rutCliente, String tipoCuenta, String claveUsuario) throws BancoException {
        Optional<Cliente> cliente = findCliente(rutCliente);

        if (cliente.isEmpty()) {
            throw new BancoException("Cliente no encontrado");
        }

        if (!cliente.get().getClavePersonal().equals(claveUsuario)) {
            throw new BancoException("Clave incorrecta");
        }

        ArrayList<Contrato> contratos = cliente.get().getContratos();
        boolean contratoEncontrado = false;

        try{
            for (Contrato contrato : contratos) {
                if (contrato.getTipoCuenta().equals(tipoCuenta)) {

                    if(contrato.isFirmadoPorCliente()){
                        throw new BancoException("El contrato ya ha sido firmado");
                    }

                    contrato.firmarContrato();
                    contratoEncontrado = true;

                    if (tipoCuenta.equals("CUENTA CORRIENTE") || tipoCuenta.equals("CUENTA RUT") || tipoCuenta.equals("CUENTA AHORRO") || tipoCuenta.equals("LINEA CREDITO")) {
                        Cuenta cuenta = new Cuenta(tipoCuenta);

                        cliente.get().agregarCuenta(cuenta);
                        cuenta.asociarCliente(cliente.get());
                    }else{ throw new BancoException("Tipo de cuenta no reconocido"); }
                    break;
                }
            }
        } catch (BancoException e) {
            throw new BancoException(e.getMessage());
        }

        System.out.println("DEBUG: Contrato firmado para el cliente " + rutCliente + " y tipo de cuenta " + tipoCuenta);
        System.out.println("DEBUG: Clave proporcionada: " + claveUsuario);
        System.out.println("DEBUG: Datos de la tarjeta del cliente");

        for (Cuenta c : cliente.get().getCuentas()){
            for (String x : c.getTarjetaAsociada().obtenerDatos()){
                System.out.println("DEBUG: " + x);
            }
        }

        return contratoEncontrado;
    }

    public double analizarSolicitud(String x) throws BancoException {
        Optional<Cliente> clienteOpt = findCliente(x);

        if (clienteOpt.isEmpty()) {
            throw new BancoException("Cliente no encontrado");
        }

        Cliente cliente = clienteOpt.get();

        System.out.println("\n--- REPORTE DE RIESGO BANCARIO ---");
        System.out.println("Cliente: " + cliente.getNombreCompleto());

        // REGLA 1: Bloqueo inmediato por historial negativo
        if (cliente.isTieneDeudaCastigada()) {
            System.out.println("[RESULTADO]: RECHAZADO AUTOMÁTICAMENTE");
            System.out.println("Motivo: El cliente presenta deuda castigada (Historial negativo).");
            cliente.setRatioEndeudamiento(-1);
            return -1;
        }

        // REGLA 2: Validación de edad mínima
        if (cliente.getEdad() < 18) {
            System.out.println("[RESULTADO]: RECHAZADO");
            System.out.println("Motivo: El cliente es menor de edad.");
            cliente.setRatioEndeudamiento(-1);
            return -1;
        }

        // REGLA 3: Cálculo de Capacidad de Endeudamiento (Ratio)
        if (cliente.getIngresosMensuales() <= 0) {
            System.out.println("[RESULTADO]: RECHAZADO");
            System.out.println("Motivo: No se registran ingresos válidos.");
            cliente.setRatioEndeudamiento(-1);
            return -1;
        }

        double ratioEndeudamiento = (cliente.getGastosMensuales() / cliente.getIngresosMensuales()) * 100;

        System.out.printf("Ratio de Endeudamiento actual: %.2f%%\n", ratioEndeudamiento);

        // Determinación del puntaje y decisión
        if (ratioEndeudamiento > 60) {
            System.out.println("Nivel de Riesgo: ALTO");
            System.out.println("[RESULTADO]: RECHAZADO");
            System.out.println("Motivo: Sus gastos superan el 60% de sus ingresos. Capacidad de pago crítica.");
            cliente.setRatioEndeudamiento(-1);
        } else if (ratioEndeudamiento > 40) {
            System.out.println("Nivel de Riesgo: MEDIO");
            System.out.println("[RESULTADO]: APROBADO CON OBSERVACIONES");
            System.out.println("Nota: Se aprueba una línea de crédito baja. Se recomienda aval.");
        } else {
            System.out.println("Nivel de Riesgo: BAJO");
            System.out.println("[RESULTADO]: APROBADO");
            System.out.println("Nota: Cliente ideal. Se ofrece línea de crédito premium.");
        }

        cliente.setRatioEndeudamiento(ratioEndeudamiento);
        return ratioEndeudamiento;
    }

    public String[][] listarClientes() {
        String[][] datosClientes = new String[clientes.size()][4];

        for (int i = 0; i < clientes.size(); i++) {
            datosClientes[i][0] = clientes.get(i).getNombreCompleto();
            datosClientes[i][1] = clientes.get(i).getRut();
            datosClientes[i][2] = clientes.get(i).getDomicilio();
            datosClientes[i][3] = clientes.get(i).getClavePersonal();
        }

        return datosClientes;
    }

    public String[][] listarContratosCliente(String rutCliente) throws BancoException {
        Optional<Cliente> cliente = findCliente(rutCliente);

        if (cliente.isEmpty()) {
            throw new BancoException("Cliente no encontrado");
        }

        ArrayList<Contrato> contratos = cliente.get().getContratos();
        String[][] datosContratos = new String[contratos.size()][2];

        for (int i = 0; i < contratos.size(); i++) {
            datosContratos[i][0] = contratos.get(i).getTipoCuenta();
            datosContratos[i][1] = contratos.get(i).isFirmadoPorCliente() ? "Firmado" : "No Firmado";
        }

        return datosContratos;
    }

    public String[][] listarCuentasYTarjetasCliente(String rutCliente) throws BancoException {
        Optional<Cliente> cliente = findCliente(rutCliente);

        if (cliente.isEmpty()) {
            throw new BancoException("Cliente no encontrado");
        }

        ArrayList<Cuenta> cuentas = cliente.get().getCuentas();
        String[][] datosCuentas = new String[cuentas.size()][4];

        for (int i = 0; i < cuentas.size(); i++) {
            datosCuentas[i][0] = cuentas.get(i).getTipoCuenta();

            Tarjeta tarjeta = cuentas.get(i).getTarjetaAsociada();

            datosCuentas[i][1] = tarjeta.getNumeroTarjeta();
            datosCuentas[i][2] = tarjeta.getFechaExpiracion();
            datosCuentas[i][3] = tarjeta.getCvv();
        }

        return datosCuentas;
    }

    public String obtenerClaveCliente(String rutCliente) throws BancoException {
        Optional<Cliente> cliente = findCliente(rutCliente);

        if (cliente.isEmpty()) {
            throw new BancoException("Cliente no encontrado");
        }

        return cliente.get().getClavePersonal();
    }
    // UTILIDADES

    private Optional<Cliente> findCliente(String rut){

        for (Cliente c : clientes){
            if (c.getRut().equals(rut)){
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    public Optional<Cliente> findClientePorNombre(String nombre) {

        // Iteramos sobre todos los clientes en la lista 'clientes'
        for (Cliente c : clientes) {

            // Comparamos el nombre del cliente con el nombre buscado.
            // Usamos equalsIgnoreCase() para ignorar mayúsculas y minúsculas
            // al comparar los nombres (ej. "Juan" es igual a "juan").
            if (c.getNombreCompleto().equalsIgnoreCase(nombre)) {
                // Si encontramos una coincidencia, devolvemos un Optional que contiene el cliente.
                return Optional.of(c);
            }
        }

        // Si el bucle termina sin encontrar coincidencias, devolvemos un Optional vacío.
        return Optional.empty();
    }


    public void saveControlador() throws BancoException {
        try{
            IOPersistencia.getInstance().saveControladores(this);
        } catch (BancoException e) {
            throw new BancoException(e.getMessage());
        }
    }

    public void readDatosSistema() throws BancoException {
        ControladorSistema controladoresIO;
        try {
            controladoresIO = IOPersistencia.getInstance().readControladores();
            if(controladoresIO != null){
                instancia = controladoresIO;
            }

        } catch (BancoException e) {
            throw new BancoException(e.getMessage());
        }
    }
}