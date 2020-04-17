package lol.cicco.ioc.core.module.register;

public class RegisterException extends RuntimeException {

    public RegisterException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RegisterException(String message) {
        super(message);
    }
}
