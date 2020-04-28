package lol.cicco.ioc.core.module.property;

import java.lang.reflect.Type;

public abstract class GeneralPropertyHandler<T> implements PropertyHandler<T> {

    protected Type bindType;

    public GeneralPropertyHandler(Type type) {
        this.bindType = type;
    }

    @Override
    public Type getType() {
        return bindType;
    }

    @Override
    public T convert(String propertyName, String propertyValue) throws PropertyConvertException {
        if (propertyValue == null) {
            return null;
        }
        try {
            return covertNonNullProperty(propertyName, propertyValue);
        } catch (Exception e) {
            throw new PropertyConvertException("Property [" + propertyName + "] 无法转换为" + getType().getTypeName() + ". PropertyValue: [" + propertyValue + "]");
        }
    }

    public abstract T covertNonNullProperty(String propertyName, String propertyValue);
}
