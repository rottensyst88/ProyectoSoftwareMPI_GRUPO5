package modelo;

import java.time.LocalDateTime;

import static java.lang.Math.random;

public class Contrato {

    private long idContrato;
    private LocalDateTime fechaContrato;
    private Cliente clienteAsociado;
    private boolean firmadoPorCliente;
    private String tipoCuenta;

    public Contrato(String tipoCuenta) {
        this.idContrato = (long) (random() * 1000000L);
        this.fechaContrato = LocalDateTime.now();
        this.clienteAsociado = null;
        firmadoPorCliente = false;
        this.tipoCuenta = tipoCuenta;
    }

    public long getIdContrato() {
        return idContrato;
    }

    public LocalDateTime getFechaContrato() {
        return fechaContrato;
    }

    public void setFechaContrato(LocalDateTime fechaContrato) {
        this.fechaContrato = fechaContrato;
    }

    public void asociarCliente(Cliente cliente){
        this.clienteAsociado = cliente;
    }

    public void firmarContrato(){
        this.firmadoPorCliente = true;
    }

    public boolean isFirmadoPorCliente() {
        return firmadoPorCliente;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }
}