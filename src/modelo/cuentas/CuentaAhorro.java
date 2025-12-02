package modelo.cuentas;

public class CuentaAhorro extends Cuenta {

    String tipoCuenta = "Ahorro";

    public CuentaAhorro(long numeroCuenta, double saldoInicial) {
        super(numeroCuenta, saldoInicial);
    }


}
