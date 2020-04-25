package lol.cicco.ioc.core.module.binder;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Property;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.property.PropertyChangeListener;
import lol.cicco.ioc.core.module.property.PropertyConvertException;
import lol.cicco.ioc.core.module.property.PropertyRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
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
        // 为当前对象添加监听器..
        registerListener(oldObj);

        if (oldObj == proxyTarget) {
            setProperty(proxyTarget, refreshBinderMap);
        } else {
            proxyTarget = oldObj;
            setProperty(proxyTarget, allBinderMap);
        }
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

            // 注册属性监听器
            final Class<?> fieldType = field.getType();
            final String listenerPropertyName = propertyName;
            final String defaultValue = defValue;
            final String listenerSign = propertyName + "-"+target.toString();
            final WeakReference<Object> binderReference = new WeakReference<>(target);
            propertyRegistry.registerPropertyListener(new PropertyChangeListener() {

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
                        propertyRegistry.removePropertyListener(propertyName(), listenerSign());
                        return;
                    }
                    if(!field.canAccess(object)) {
                        field.setAccessible(true);
                    }

                    Object propertyValue = propertyRegistry.convertValue(listenerPropertyName, defaultValue, fieldType);

                    try {
                        field.set(object, propertyValue);
                    } catch (Exception e) {
                        log.error("RefreshProperty出现异常, 异常信息:{}", e.getMessage(), e);
                    }
                }
            });
        }
    }

    private String getDefaultValue(Binder binder) {
        return "".equals(binder.defaultValue().trim()) ? null : binder.defaultValue().trim();
    }
}
