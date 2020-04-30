package lol.cicco.ioc.core.module.binder;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Property;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.property.PropertyChangeListener;
import lol.cicco.ioc.core.module.property.PropertyConvertException;
import lol.cicco.ioc.core.module.property.PropertyRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BinderBeanProvider implements BeanProvider {
    private static final Queue<PropertyChangeListener> REGISTER_LISTENERS = new ConcurrentLinkedQueue<>();
    private final BeanProvider beanProvider;
    private final PropertyRegistry propertyRegistry;

    // 注入属性
    private final Map<Field, Binder> allBinderMap;
    private final Map<Field, Binder> refreshBinderMap;
    private final Property property;

    private Object proxyTarget = null;

    static {
        checkListeners();
    }

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
            injectProperty(proxyTarget, refreshBinderMap);
        } else {
            proxyTarget = oldObj;
            injectProperty(proxyTarget, allBinderMap);
        }
        return proxyTarget;
    }

    @SneakyThrows
    private void injectProperty(Object target, Map<Field, Binder> binderMap) {
        String prefix = this.property == null ? "" : this.property.prefix().trim();

        for (Field field : binderMap.keySet()) {
            Binder fieldBinder = binderMap.get(field);
            String propertyName = prefix;
            String defValue = null;
            boolean noValueToNull = false;
            if (fieldBinder != null) {
                propertyName += fieldBinder.value().trim();
                defValue = getDefaultValue(fieldBinder);
                noValueToNull = fieldBinder.noValueToNull();
            } else {
                propertyName = propertyName + "." + field.getName();
            }

            if (!field.canAccess(target)) {
                field.setAccessible(true);
            }
            Object propertyValue = propertyRegistry.convertValue(propertyName, defValue, field.getType());

            if (!noValueToNull && propertyValue == null && this.property == null) {
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
        for (Field field : refreshBinderMap.keySet()) {
            Binder fieldBinder = refreshBinderMap.get(field);

            String propertyName = prefix;
            String defValue = null;
            boolean noValueToNull = false;
            if (fieldBinder != null) {
                propertyName += fieldBinder.value().trim();
                defValue = getDefaultValue(fieldBinder);
                noValueToNull = fieldBinder.noValueToNull();
            } else {
                propertyName = propertyName + "." + field.getName();
            }

            // 注册属性监听器
            BinderPropertyChangeListener changeListener = new BinderPropertyChangeListener(noValueToNull, target, field, propertyName, defValue, propertyRegistry);
            propertyRegistry.registerPropertyListener(changeListener);
            REGISTER_LISTENERS.add(changeListener);
        }
    }

    private String getDefaultValue(Binder binder) {
        return "".equals(binder.defaultValue().trim()) ? null : binder.defaultValue().trim();
    }

    // 检测对象
    private static void checkListeners() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable runnable) {
                counter.compareAndSet(Integer.MAX_VALUE, 0);
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                thread.setName("Schedule Thread-" + counter.addAndGet(1));
                return thread;
            }
        });
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.debug("开始检测已注册PropertyListeners存储对象是否还存活.....");
            for (PropertyChangeListener listener : REGISTER_LISTENERS) {
                if (listener instanceof BinderPropertyChangeListener) {
                    BinderPropertyChangeListener propertyChangeListener = (BinderPropertyChangeListener) listener;
                    if (propertyChangeListener.getBinderReference().get() == null) {
                        log.debug("检测到Listener[{}]已经失效....", propertyChangeListener.listenerSign());
                        // 对象已被回收...
                        propertyChangeListener.removeListener();
                        REGISTER_LISTENERS.remove(propertyChangeListener);
                    }
                }
            }
        }, 0, 30, TimeUnit.MINUTES);

        Runtime.getRuntime().addShutdownHook(new Thread(scheduledExecutorService::shutdown));
    }


}
