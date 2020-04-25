package lol.cicco.ioc.core.module.property;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
class PropertyListeners {

    private static final Map<String, List<PropertyChangeListener>> objectListeners = new LinkedHashMap<>();
    private final PropertyRegistry registry;

    static {
        checkListeners();
    }

    PropertyListeners(PropertyRegistry registry) {
        this.registry = registry;
    }

    void register(PropertyChangeListener listener) {
        synchronized (objectListeners) {
            List<PropertyChangeListener> objects = objectListeners.getOrDefault(listener.getProperty(), new LinkedList<>());
            if(objects.stream().map(r -> r.getObject().get()).filter(Objects::nonNull).noneMatch(a-> listener.getObject().get() == a)) {
                objects.add(listener);
                log.debug("PropertyListener 监听[{}] , 当前对象数量为:{}", listener.toString(), objects.size());
                objectListeners.put(listener.getProperty(), objects);
            }
        }
    }

    void onChange(String propertyName) {
        synchronized (objectListeners) {
            log.debug("onChange:[{}]....", propertyName);
            List<PropertyChangeListener> objects = objectListeners.get(propertyName);
            if(objects != null && !objects.isEmpty()) {
                Iterator<PropertyChangeListener> iterator = objects.iterator();
                while(iterator.hasNext()) {
                    PropertyChangeListener listener = iterator.next();
                    Object target = listener.getObject().get();
                    Field field = listener.getField();
                    if (target == null) {
                        log.debug("Listener[{}]目标对象已经被回收.....", listener.toString());
                        iterator.remove();
                        continue;
                    }
                    if(!field.canAccess(target)) {
                        field.setAccessible(true);
                    }

                    Object propertyValue = registry.convertValue(propertyName, listener.getDefaultValue(), listener.getField().getType());

                    try {
                        field.set(target, propertyValue);
                    } catch (Exception e) {
                        log.error("RefreshProperty出现异常, 异常信息:{}", e.getMessage(), e);
                    }
                }
            }
        }
    }

    private static void checkListeners() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            synchronized (objectListeners) {
                log.debug("开始检测已注册objectListeners存储对象是否还存活.....");
                Iterator<String> iterator = objectListeners.keySet().iterator();
                while(iterator.hasNext()) {
                    String key = iterator.next();
                    List<PropertyChangeListener> listeners = objectListeners.get(key);
                    if(listeners == null || listeners.isEmpty()) {
                        iterator.remove();
                        continue;
                    }
                    listeners.removeIf(propertyChangeListener -> propertyChangeListener.getObject().get() == null);
                }
            }
        },0, 10, TimeUnit.MINUTES);

        Runtime.getRuntime().addShutdownHook(new Thread(scheduledExecutorService::shutdown));
    }


}
