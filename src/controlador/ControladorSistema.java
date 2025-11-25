package controlador;

public class ControladorSistema {

    private static ControladorSistema instancia;

    private ControladorSistema() {}

    public static ControladorSistema getInstancia() {
        if (instancia == null) {
            instancia = new ControladorSistema();
        }
        return instancia;
    }
}