package lol.cicco.ioc.core.module.property;

import java.lang.reflect.Type;

public interface PropertyHandler<T> {

    /**
     * 获取属性转换类型
     */
    Type getType();

    /**
     * 执行属性转换
     */
    T convert(String propertyName, String propertyValue) throws PropertyConvertException;
}
