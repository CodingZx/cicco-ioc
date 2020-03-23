package lol.cicco.ioc.core.exception;

public class BeanInitializeException extends RuntimeException {

    public BeanInitializeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BeanInitializeException(String message) {
        super(message);
    }
}
