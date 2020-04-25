package lol.cicco.ioc.core.module.property;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
class PropertyListeners {

    private static final Map<String, List<PropertyChangeListener>> objectListener = new LinkedHashMap<>();
    private final PropertyRegistry registry;

    PropertyListeners(PropertyRegistry registry) {
        this.registry = registry;
    }

    void register(PropertyChangeListener listener) {
        synchronized (objectListener) {
            List<PropertyChangeListener> objects = objectListener.getOrDefault(listener.getProperty(), new LinkedList<>());
            if(objects.stream().map(r -> r.getObject().get()).filter(Objects::nonNull).noneMatch(a-> listener.getObject().get() == a)) {
                objects.add(listener);
                log.debug("PropertyListener 监听[{}] , 当前对象数量为:{}", listener.toString(), objects.size());
                objectListener.put(listener.getProperty(), objects);
            }
        }
    }

    void onChange(String propertyName) {
        synchronized (objectListener) {
            log.debug("onChange:[{}]....", propertyName);
            List<PropertyChangeListener> objects = objectListener.get(propertyName);
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



}
