import vista.Vista;

public class VistaLoader {
    public static void main(String[] args) {
        Vista vista = Vista.getInstancia();
        vista.menu();
    }
}
