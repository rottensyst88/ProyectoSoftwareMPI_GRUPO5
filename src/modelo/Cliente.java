package modelo;

import excepcion.BancoException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;


public class Cliente implements Serializable {
    private String rut;
    private String domicilio;
    private String nombreCompleto;
    private ArrayList<Contrato> contratos;
    private ArrayList<Cuenta> cuentas;
    private String clavePersonal;

    public Cliente(String nombreCompleto, String rut, String domicilio) {
        this.rut = rut;
        this.domicilio = domicilio;
        this.nombreCompleto = nombreCompleto;
        this.contratos = new ArrayList<>();
        this.cuentas = new ArrayList<>();
        this.clavePersonal = getRandomString();
    }

    public String getClavePersonal() {
        return clavePersonal;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public void agregarContrato(Contrato contrato) throws BancoException {
        if (contrato == null) {
            throw new BancoException("El contrato no puede ser nulo");
        }

        for (Contrato c : contratos) {
            if (c.getTipoCuenta().equals(contrato.getTipoCuenta())) {
                throw new BancoException("El cliente ya tiene un contrato de este tipo de cuenta");
            }
        }

        if (contratos.size() > 2) {
            throw new BancoException("El cliente no puede tener más de 3 contratos");
        }

        contratos.add(contrato);
        contrato.asociarCliente(this);
    }

    public ArrayList<Contrato> getContratos() {
        return contratos;
    }

    public ArrayList<Cuenta> getCuentas() {
        return cuentas;
    }

    public void agregarCuenta(Cuenta cuenta) throws BancoException {
        if (cuenta == null) {
            throw new BancoException("La cuenta no puede ser nula");
        }

        for (Cuenta c : cuentas) {
            if (c.getTipoCuenta().equals(cuenta.getTipoCuenta())) {
                throw new BancoException("El cliente ya tiene una cuenta de este tipo");
            }
        }

        if (cuentas.size() > 2) {
            throw new BancoException("El cliente no puede tener más de 3 cuentas");
        }

        cuentas.add(cuenta);
        cuenta.asociarCliente(this);
    }

    // Todo Aclarar esto metodo!

    public void datosTelefono(){
        System.out.println("Cliente");
    }

    public void realizarDeposito(){
        System.out.println("Realiza pago");
    }

    public void sacarDinero(){
        System.out.println("Saca dinero");
    }

    public String getRandomString(){
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}