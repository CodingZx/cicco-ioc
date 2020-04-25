package lol.cicco.ioc.core.module.binder;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Property;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.property.PropertyChangeListener;
import lol.cicco.ioc.core.module.property.PropertyConvertException;
import lol.cicco.ioc.core.module.property.PropertyRegistry;
import lombok.SneakyThrows;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class BinderBeanProvider implements BeanProvider {
    private final BeanProvider beanProvider;
    private final PropertyRegistry propertyRegistry;

    // 注入属性
    private final Map<Field, Binder> allBinderMap;
    private final Map<Field, Binder> refreshBinderMap;
    private final Property property;

    private Object proxyTarget = null;

    BinderBeanProvider(BeanProvider beanProvider, PropertyRegistry propertyRegistry) {
        this.beanProvider = beanProvider;
        this.propertyRegistry = propertyRegistry;
        this.property = beanType().getDeclaredAnnotation(Property.class);
        this.allBinderMap = new LinkedHashMap<>();
        this.refreshBinderMap = new LinkedHashMap<>();

        // 分析注入属性
        analyzeBinder();
    }

    @Override
    public Class<?> beanType() {
        return beanProvider.beanType();
    }

    @Override
    public Object getObject() {
        if (allBinderMap.isEmpty()) {
            // 不需要进行binder注入.
            return beanProvider.getObject();
        }

        // 执行延迟注入..
        Object oldObj = beanProvider.getObject();

        registerListener(oldObj);

        if (oldObj == proxyTarget) {
            // refresh...
            setProperty(proxyTarget, refreshBinderMap);
            return proxyTarget;
        }
        proxyTarget = oldObj;
        // 第一次初始化时注入属性
        setProperty(proxyTarget, allBinderMap);

        return proxyTarget;
    }

    @SneakyThrows
    private void setProperty(Object target, Map<Field, Binder> binderMap) {
        String prefix = this.property == null ? "" : this.property.prefix().trim();

        for (Field field : binderMap.keySet()) {
            Binder fieldBinder = binderMap.get(field);
            String propertyName = prefix;
            String defValue = null;
            if (fieldBinder != null) {
                propertyName += fieldBinder.value().trim();
                defValue = getDefaultValue(fieldBinder);
            } else {
                propertyName = propertyName + "." + field.getName();
            }

            if (!field.canAccess(target)) {
                field.setAccessible(true);
            }
            Object propertyValue = propertyRegistry.convertValue(propertyName, defValue, field.getType());

            if (propertyValue == null && this.property == null) {
                throw new PropertyConvertException("Property [" + propertyName + "] 未配置, 请检查对应配置文件...");
            }
            field.set(target, propertyValue);
        }
    }

    private void analyzeBinder() {
        for (Field field : beanType().getDeclaredFields()) {
            Binder binder = field.getDeclaredAnnotation(Binder.class);

            if (property == null && binder == null) {
                continue;
            }
            allBinderMap.put(field, binder);
            if ((property != null && property.refresh()) || (binder != null && binder.refresh())) {
                refreshBinderMap.put(field, binder);
            }
        }
    }

    private void registerListener(Object target) {
        String prefix = this.property == null ? "" : this.property.prefix().trim();
        for(Field field : refreshBinderMap.keySet()) {
            Binder fieldBinder = refreshBinderMap.get(field);

            String propertyName = prefix;
            String defValue = null;
            if (fieldBinder != null) {
                propertyName += fieldBinder.value().trim();
                defValue = getDefaultValue(fieldBinder);
            } else {
                propertyName = propertyName + "." + field.getName();
            }

            PropertyChangeListener listener = new PropertyChangeListener();
            listener.setDefaultValue(defValue);
            listener.setField(field);
            listener.setObject(new WeakReference<>(target));
            listener.setProperty(propertyName);
            propertyRegistry.registerPropertyListener(listener);
        }
    }

    private String getDefaultValue(Binder binder) {
        return "".equals(binder.defaultValue().trim()) ? null : binder.defaultValue().trim();
    }
}
