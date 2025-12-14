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

    private int edad;
    private double ingresosMensuales;
    private double gastosMensuales;
    private boolean tieneDeudaCastigada;

    private double ratioEndeudamiento;

    public Cliente(String nombreCompleto, String rut, String domicilio, int edad, double ingresos, double gastos, boolean castigada) {
        this.rut = rut;
        this.domicilio = domicilio;
        this.nombreCompleto = nombreCompleto;
        this.contratos = new ArrayList<>();
        this.cuentas = new ArrayList<>();
        this.clavePersonal = getRandomString();

        this.edad = edad;
        this.ingresosMensuales = ingresos;
        this.gastosMensuales = gastos;
        this.tieneDeudaCastigada = castigada;

        ratioEndeudamiento = -1;
    }

    public void setRatioEndeudamiento(double ratioEndeudamiento) {
        this.ratioEndeudamiento = ratioEndeudamiento;
    }

    public double getRatioEndeudamiento() {
        return ratioEndeudamiento;
    }

    public int getEdad() { return edad; }

    public double getIngresosMensuales() { return ingresosMensuales; }

    public double getGastosMensuales() { return gastosMensuales; }

    public boolean isTieneDeudaCastigada() { return tieneDeudaCastigada; }

    public String getClavePersonal() {
        return clavePersonal;
    }

    public String getRut() {
        return rut;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
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

        if (cuentas.size() > 3) {
            throw new BancoException("El cliente no puede tener más de 4 cuentas");
        }

        cuentas.add(cuenta);
        cuenta.asociarCliente(this);
    }

    public String getRandomString(){
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}