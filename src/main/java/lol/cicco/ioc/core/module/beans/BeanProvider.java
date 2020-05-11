package lol.cicco.ioc.core.module.beans;

public interface BeanProvider {

    Class<?> beanType();

    Object getObject();

    void initialize();
}
