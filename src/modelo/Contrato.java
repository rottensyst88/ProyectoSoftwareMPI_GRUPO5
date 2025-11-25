package modelo;

import java.time.LocalDateTime;

public class Contrato {

    private long idContrato;
    private LocalDateTime fechaContrato;
    private Contrato contrato; // QUE ES ESTO!

    public Contrato(long idContrato, LocalDateTime fechaContrato, Contrato contrato) {
        this.idContrato = idContrato;
        this.fechaContrato = fechaContrato;
        this.contrato = contrato;
    }

    public long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(long idContrato) {
        this.idContrato = idContrato;
    }

    public LocalDateTime getFechaContrato() {
        return fechaContrato;
    }

    public void setFechaContrato(LocalDateTime fechaContrato) {
        this.fechaContrato = fechaContrato;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }
}