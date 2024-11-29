package cat.uvic.teknos.shoeshop.clients.exceptions;

public class ConsoleClientException extends RuntimeException {
    public ConsoleClientException() {
    }

    public ConsoleClientException(String message) {
        super(message);
    }

    public ConsoleClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsoleClientException(Throwable cause) {
        super(cause);
    }

    public ConsoleClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
