package lol.cicco.ioc.core.binder;

import lol.cicco.ioc.core.exception.PropertyBindException;

import java.lang.reflect.Type;

public abstract class GeneralBindHandler<T> implements BindHandler<T> {

    protected Type bindType;

    public GeneralBindHandler(Type type) {
        this.bindType = type;
    }

    @Override
    public Type bindType() {
        return bindType;
    }

    @Override
    public T convert(String propertyName, String propertyValue) throws PropertyBindException {
        try {
            return covertProperty(propertyName, propertyValue);
        } catch (Exception e) {
            throw new PropertyBindException("Property [" + propertyName + "] 无法转换为"+bindType().getTypeName()+". PropertyValue: [" + propertyValue + "]");
        }
    }

    public abstract T covertProperty(String propertyName, String propertyValue) ;
}
