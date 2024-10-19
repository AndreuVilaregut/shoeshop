package cat.teknos.shoeshop.services.exeption;

public class ResourceNotFoundExeption extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundExeption() {
    }

    public ResourceNotFoundExeption(String message) {
        super(message);
    }

    public ResourceNotFoundExeption(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundExeption(Throwable cause) {
        super(cause);
    }

    public ResourceNotFoundExeption(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
