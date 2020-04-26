package lol.cicco.ioc.core.module.binder;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.core.module.property.PropertyChangeListener;
import lol.cicco.ioc.core.module.property.PropertyRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

@Slf4j
class BinderPropertyChangeListener implements PropertyChangeListener {
    private final boolean noValueToNull;
    private final Class<?> fieldType;
    private final Field field;
    private final String listenerPropertyName;
    private final String defaultValue;
    private final String listenerSign;
    @Getter
    private final WeakReference<Object> binderReference;
    private final PropertyRegistry registry;

    BinderPropertyChangeListener(boolean noValueToNull, Object target, Field field, String propertyName, String defValue, PropertyRegistry registry) {
        this.noValueToNull = noValueToNull;
        this.fieldType = field.getType();
        this.field = field;
        this.listenerPropertyName = propertyName;
        this.listenerSign = propertyName + "-" + target.toString();
        this.binderReference = new WeakReference<>(target);
        this.defaultValue = defValue;
        this.registry = registry;
    }

    @Override
    public String propertyName() {
        return listenerPropertyName;
    }

    @Override
    public String listenerSign() {
        return listenerSign;
    }

    @Override
    public void onChange() {
        Object object = binderReference.get();
        if(object == null) {
            // 对象已经被垃圾回收.. 移除属性监听器...
            removeListener();
            return;
        }
        if(!field.canAccess(object)) {
            field.setAccessible(true);
        }

        Object propertyValue = registry.convertValue(listenerPropertyName, defaultValue, fieldType);
        try {
            if(propertyValue == null && !noValueToNull) {
                log.warn("Property [{}] 属性值为Null", propertyName());
            } else {
                field.set(object, propertyValue);
            }
        } catch (Exception e) {
            log.warn("RefreshProperty出现异常, 异常信息:{}", e.getMessage(), e);
        }
    }

    public void removeListener() {
        registry.removePropertyListener(propertyName(), listenerSign());
    }
}
