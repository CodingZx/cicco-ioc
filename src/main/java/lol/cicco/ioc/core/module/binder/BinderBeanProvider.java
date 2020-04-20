package lol.cicco.ioc.core.module.binder;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Property;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.property.PropertyConvertException;
import lol.cicco.ioc.core.module.property.PropertyRegistry;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class BinderBeanProvider implements BeanProvider {
    private final boolean hasBinder; // 是否需要Binder注入
    private final BeanProvider beanProvider;
    private final PropertyRegistry propertyRegistry;

    private Object target = null;

    BinderBeanProvider(BeanProvider beanProvider, PropertyRegistry propertyRegistry) {
        this.beanProvider = beanProvider;
        this.propertyRegistry = propertyRegistry;
        this.hasBinder = checkHasBinder();
    }

    private boolean checkHasBinder() {
        Class<?> beanType = beanProvider.beanType();
        Property beanProperty = beanType.getDeclaredAnnotation(Property.class);
        if(beanProperty != null) {
            return true;
        }
        for(Field field : beanType.getDeclaredFields()) {
            Binder binder = field.getDeclaredAnnotation(Binder.class);
            if(binder != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Class<?> beanType() {
        return beanProvider.beanType();
    }

    @Override
    @SneakyThrows
    public Object getObject() {
        if(!hasBinder) {
            // 不需要进行binder注入.
            return beanProvider.getObject();
        }

        Object oldObj = beanProvider.getObject();
        if(oldObj == target) {
            return target;
        }
        target = oldObj;

        Class<?> beanType = beanProvider.beanType();

        Property beanProperty = beanType.getDeclaredAnnotation(Property.class);
        String prefix = beanProperty == null ? "" : beanProperty.prefix().trim();

        for(Field field : beanType.getDeclaredFields()) {
            Binder binder = field.getDeclaredAnnotation(Binder.class);

            if(beanProperty == null && binder == null) {
                continue;
            }

            String propertyName = prefix;
            String defValue = null;
            if(binder != null) {
                propertyName += binder.value().trim();
                defValue = "".equals(binder.defaultValue().trim()) ? null : binder.defaultValue().trim();
            } else {
                propertyName = propertyName + "." + field.getName();
            }

            if(!field.canAccess(target)) {
                field.setAccessible(true);
            }
            Object propertyValue = propertyRegistry.convertValue(propertyName, defValue, field.getType());

            if (propertyValue == null && beanProperty == null) {
                throw new PropertyConvertException("Property [" + propertyName + "] 未配置, 请检查对应配置文件...");
            }
            field.set(target, propertyValue);
        }
        return target;
    }

}
