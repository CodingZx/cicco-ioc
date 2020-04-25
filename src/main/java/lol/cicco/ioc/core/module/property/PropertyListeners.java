package lol.cicco.ioc.core.module.property;

import lombok.extern.slf4j.Slf4j;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
class PropertyListeners {

    private static final Map<String, List<InlinePropertyListener>> objectListeners = new LinkedHashMap<>();

    static {
        checkListeners();
    }

    private static class InlinePropertyListener {
        private WeakReference<?> target;
        private OnChangeFunc processor;
    }

    void register(PropertyChangeListener listener) {
        synchronized (objectListeners) {
            List<InlinePropertyListener> objects = objectListeners.getOrDefault(listener.propertyName(), new LinkedList<>());
            if(objects.stream().map(r -> r.target.get()).filter(Objects::nonNull).noneMatch(a-> listener.getObject() == a)) {
                InlinePropertyListener propertyListener = new InlinePropertyListener();
                propertyListener.target = new WeakReference<>(listener.getObject());
                propertyListener.processor = listener::onChange;
                objects.add(propertyListener);
                log.debug("PropertyListener 监听[{}] , 当前对象数量为:{}", listener.toString(), objects.size());
                objectListeners.put(listener.propertyName(), objects);
            }
        }
    }

    void onChange(String propertyName) {
        synchronized (objectListeners) {
            log.debug("onChange:[{}]....", propertyName);
            List<InlinePropertyListener> objects = objectListeners.get(propertyName);
            if(objects != null && !objects.isEmpty()) {
                Iterator<InlinePropertyListener> iterator = objects.iterator();
                while(iterator.hasNext()) {
                    InlinePropertyListener listener = iterator.next();
                    Object target = listener.target.get();
                    if (target == null) {
                        log.debug("Listener[{}]目标对象已经被回收.....", listener.toString());
                        iterator.remove();
                        continue;
                    }

                    listener.processor.onChange();
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
                    List<InlinePropertyListener> listeners = objectListeners.get(key);
                    if(listeners == null || listeners.isEmpty()) {
                        iterator.remove();
                        continue;
                    }
                    listeners.removeIf(listener -> listener.target.get() == null);
                }
            }
        },0, 10, TimeUnit.MINUTES);

        Runtime.getRuntime().addShutdownHook(new Thread(scheduledExecutorService::shutdown));
    }


}
