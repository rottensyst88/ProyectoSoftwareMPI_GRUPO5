package modelo;

import java.io.Serializable;

public class Cuenta implements Serializable {

    private Cliente clienteAsociado;
    private long idCuenta;
    private double saldo;
    private String tipoCuenta;
    private Tarjeta tarjetaAsociada;

    public Cuenta(String tipoCuenta) {
        this.idCuenta = 0;
        this.saldo = 0;
        this.tipoCuenta = tipoCuenta;
        this.clienteAsociado = null;
        this.tarjetaAsociada = Tarjeta.generar();
    }

    public Tarjeta getTarjetaAsociada() {
        return tarjetaAsociada;
    }


    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(long idCuenta) {
        this.idCuenta = idCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    //TODO Aclarar esto metodo!

    public void nombreMiembro(){
        System.out.println("Cuenta");
    }

    public void asociarCliente(Cliente cliente){
        this.clienteAsociado = cliente;
    }


}
