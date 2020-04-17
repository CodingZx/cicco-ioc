package lol.cicco.ioc.core.module.beans;

public class BeanStoreException extends RuntimeException {

    public BeanStoreException(String message) {
        super(message);
    }

    public BeanStoreException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
