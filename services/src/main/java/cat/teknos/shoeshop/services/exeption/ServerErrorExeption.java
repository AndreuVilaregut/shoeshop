package cat.teknos.shoeshop.services.exeption;

public class ServerErrorExeption extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServerErrorExeption() {
    }

    public ServerErrorExeption(String message) {
        super(message);
    }

    public ServerErrorExeption(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerErrorExeption(Throwable cause) {
        super(cause);
    }

    public ServerErrorExeption(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
