package excepcion;

public class BancoException extends RuntimeException {
    public BancoException(String message) {
        super(message);
    }
}
