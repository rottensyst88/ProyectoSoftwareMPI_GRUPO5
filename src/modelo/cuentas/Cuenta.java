package modelo.cuentas;

public abstract class Cuenta {

    private long idCuenta;
    private double saldo;

    public Cuenta(long idCuenta, double saldo) {
        this.idCuenta = idCuenta;
        this.saldo = saldo;
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
}
