package controlador;

import modelo.Cliente;
import excepcion.BancoException;
import modelo.Contrato;
import modelo.cuentas.Cuenta;

import java.util.ArrayList;
import java.util.Optional;

public class ControladorSistema {

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
    ArrayList<Cuenta> cuentas = new ArrayList<>();

    public void crearCliente(String nombre, String rut, String domicilio) throws BancoException {

        if (findCliente(rut).isPresent()) {
            throw new BancoException("Cliente ya existe");
        }

        Cliente cliente = new Cliente(nombre, rut, domicilio);
        clientes.add(cliente);

    }

    public void generarContratoCliente(String clienteRut, String tipoCuenta) throws BancoException {

        Optional<Cliente> cliente = findCliente(clienteRut);

        if (cliente.isPresent()) {

            Contrato contrato = new Contrato(tipoCuenta);

            /*
            if (cliente.get().getContratos().contains(contrato)) {
                throw new BancoException("El contrato ya existe para este cliente");
            }*/

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
                    contrato.firmarContrato();
                    contratoEncontrado = true;
                    break;
                }
            }
        } catch (BancoException e) {
            throw new BancoException(e.getMessage());
        }

        return contratoEncontrado;
    }

    public String[][] listarClientes() {
        String[][] datosClientes = new String[clientes.size()][3];

        for (int i = 0; i < clientes.size(); i++) {
            datosClientes[i][0] = clientes.get(i).getNombreCompleto();
            datosClientes[i][1] = clientes.get(i).getRut();
            datosClientes[i][2] = clientes.get(i).getDomicilio();
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

    public String obtenerClaveCliente(String rutCliente) throws BancoException {
        Optional<Cliente> cliente = findCliente(rutCliente);

        if (cliente.isEmpty()) {
            throw new BancoException("Cliente no encontrado");
        }

        return cliente.get().getClavePersonal();
    }

    public boolean verificarClaveCliente(String rutCliente, String clave) throws BancoException {
        Optional<Cliente> cliente = findCliente(rutCliente);

        if (cliente.isEmpty()) {
            throw new BancoException("Cliente no encontrado");
        }

        return cliente.get().getClavePersonal().equals(clave);
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

}