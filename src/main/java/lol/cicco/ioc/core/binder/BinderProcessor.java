package lol.cicco.ioc.core.binder;

import lol.cicco.ioc.core.exception.PropertyBindException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public final class BinderProcessor {
    private static final Map<Type, List<BindHandler<?>>> bindHandler = new LinkedHashMap<>();

    private static final BinderProcessor OBJECT = new BinderProcessor();

    private BinderProcessor() {
        registerHandler(NumberBindHandler.create());
        registerHandler(StringBindHandler.create());
        registerHandler(Java8TimeBindHandler.create());
        registerHandler(UUIDBindHandler.create());
    }

    public static BinderProcessor getInstance() {
        return OBJECT;
    }

    public void registerHandler(BindHandler<?> handler) {
        registerHandler(Collections.singleton(handler));
    }

    public void registerHandler(Collection<BindHandler<?>> handlerArr) {
        for(BindHandler<?> handler : handlerArr) {
            log.debug("Binder处理器注册类型[{}], 对应处理器[{}]", handler.bindType().getTypeName(), handler.getClass().toString());
            var handlers = bindHandler.getOrDefault(handler.bindType(), new LinkedList<>());
            handlers.add(handler);
            bindHandler.put(handler.bindType(), handlers);
        }
    }

    public <T> T covertValue(String propName, String propValue, Type type) {
        var handlers = bindHandler.getOrDefault(type, new ArrayList<>());
        PropertyBindException exception = null;
        for (BindHandler<?> handler : handlers) {
            try {
                return (T) handler.convert(propName, propValue);
            } catch (PropertyBindException e) {
                exception = e;
            }
        }
        if (exception == null) {
            exception = new PropertyBindException("BindProcessor无法转换[" + type.getTypeName() + "], 对应属性名称:[" + propName + "], 属性值:[" + propValue + "]");
        }
        throw exception;
    }

}
