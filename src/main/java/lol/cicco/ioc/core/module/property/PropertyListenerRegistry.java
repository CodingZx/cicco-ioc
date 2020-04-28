package lol.cicco.ioc.core.module.property;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
class PropertyListenerRegistry {

    private static final Map<String, Queue<InlinePropertyListener>> objectListeners = new LinkedHashMap<>();

    private static class InlinePropertyListener {
        private Runnable processor;
        private String listenerSign;
    }

    void register(PropertyChangeListener listener) {
        synchronized (objectListeners) {
            Queue<InlinePropertyListener> objects = objectListeners.getOrDefault(listener.propertyName(), new ConcurrentLinkedQueue<>());
            if (objects.stream().noneMatch(a -> listener.listenerSign().equals(a.listenerSign))) {
                InlinePropertyListener propertyListener = new InlinePropertyListener();
                propertyListener.processor = listener::onChange;
                propertyListener.listenerSign = listener.listenerSign();
                objects.add(propertyListener);
                log.debug("PropertyListener 监听属性[{}] sign:{}, 当前对象数量为:{}", listener.propertyName(), listener.listenerSign(), objects.size());
                objectListeners.put(listener.propertyName(), objects);
            }
        }
    }

    void removeListener(String propertyName, String sign) {
        synchronized (objectListeners) {
            Queue<InlinePropertyListener> objects = objectListeners.get(propertyName);
            boolean removeFlag = objects.removeIf(inlinePropertyListener -> inlinePropertyListener.listenerSign.equals(sign));
            if (removeFlag) {
                log.debug("Property[{}] Listener[{}]被移除..", propertyName, sign);
            }
        }
    }

    void onChange(String propertyName) {
        synchronized (objectListeners) {
            log.debug("onChange:[{}]....", propertyName);
            Queue<InlinePropertyListener> objects = objectListeners.get(propertyName);
            if (objects != null && !objects.isEmpty()) {
                for (InlinePropertyListener listener : objects) {
                    listener.processor.run();
                }
            }
        }
    }

}
