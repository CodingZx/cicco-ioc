package lol.cicco.ioc.core;

public class BeanDefinitionStoreException extends RuntimeException {

    public BeanDefinitionStoreException(String message){
        super(message);
    }

    public BeanDefinitionStoreException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
