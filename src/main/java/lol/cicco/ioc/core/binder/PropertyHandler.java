package lol.cicco.ioc.core.binder;

import lol.cicco.ioc.core.exception.PropertyConvertException;

import java.lang.reflect.Type;

public interface PropertyHandler<T> {

    Type getType();

    T convert(String propertyName, String propertyValue) throws PropertyConvertException;
}
