package lol.cicco.ioc.core.binder;

import lol.cicco.ioc.core.exception.PropertyBindException;

import java.lang.reflect.Type;

public interface BindHandler<T> {

    Type bindType();

    T convert(String propertyName, String propertyValue) throws PropertyBindException;
}
