package modelo;

public class Cliente {
    private String rut;
    private String domicilio;
    private String nombreCompleto;

    public Cliente(String rut, String domicilio, String nombreCompleto) {
        this.rut = rut;
        this.domicilio = domicilio;
        this.nombreCompleto = nombreCompleto;
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

    // Todo Aclarar esto metodo!

    public void datosTelefono(){
        System.out.println("Cliente");
    }

    public void firmaContrato(){
        System.out.println("Firma contrato");
    }

    public void realizarDeposito(){
        System.out.println("Realiza pago");
    }

    public void sacarDinero(){
        System.out.println("Saca dinero");
    }
}