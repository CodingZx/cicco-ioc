package lol.cicco.ioc.core.module.binder;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.property.PropertyConvertException;
import lol.cicco.ioc.core.module.property.PropertyRegistry;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class BinderBeanProvider implements BeanProvider {
    private final BeanProvider beanProvider;
    private final PropertyRegistry propertyRegistry;

    private Object target = null;

    BinderBeanProvider(BeanProvider beanProvider, PropertyRegistry propertyRegistry) {
        this.beanProvider = beanProvider;
        this.propertyRegistry = propertyRegistry;
    }

    @Override
    public Class<?> beanType() {
        return beanProvider.beanType();
    }

    @Override
    @SneakyThrows
    public Object getObject() {
        Object oldObj = beanProvider.getObject();
        if(oldObj == target) {
            return target;
        }
        target = oldObj;

        for(Field field : beanProvider.beanType().getDeclaredFields()) {
            Binder binder = field.getDeclaredAnnotation(Binder.class);
            if(binder == null) {
                continue;
            }

            String defValue = "".equals(binder.defaultValue().trim()) ? null : binder.defaultValue().trim();

            if(!field.canAccess(target)) {
                field.setAccessible(true);
            }
            Object propertyValue = propertyRegistry.covertValue(binder.value().trim(), defValue, field.getType());

            if (propertyValue == null) {
                throw new PropertyConvertException("Property [" + binder.value() + "] 未配置, 请检查对应配置文件...");
            }

            field.set(target, propertyValue);
        }
        return target;
    }
}
