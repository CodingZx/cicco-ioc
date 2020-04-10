package lol.cicco.ioc.core.binder;

import lol.cicco.ioc.core.exception.PropertyConvertException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public final class PropertyProcessor {
    // 属性转换器存在Map, 允许同一种类型对应多个转换器
    private final Map<Type, Collection<PropertyHandler<?>>> PROPERTY_HANDLERS = new LinkedHashMap<>();

    private static final Collection<PropertyHandler<?>> EMPTY_COLLECTION = new ArrayList<>();

    private PropertyProcessor() {
        // 提供注册基本类型及相关JDK中类型的转换器
        registerHandler(NumberPropertyHandler.create());
        registerHandler(StringPropertyHandler.create());
        registerHandler(Java8TimePropertyHandler.create());
        registerHandler(UUIDPropertyHandler.create());
    }

    /**
     * 获取全局唯一实例
     */
    public static PropertyProcessor getInstance() {
        return new PropertyProcessor();
    }

    /**
     * 注册自定义属性处理器
     */
    public void registerHandler(PropertyHandler<?> handler) {
        registerHandler(Collections.singleton(handler));
    }

    /**
     * 注册自定义属性处理器
     */
    public void registerHandler(Collection<PropertyHandler<?>> handlers) {
        for (PropertyHandler<?> handler : handlers) {
            log.debug("PropertyHandler注册类型[{}], 对应处理器[{}]", handler.getType().getTypeName(), handler.getClass().toString());
            var bindHandlers = PROPERTY_HANDLERS.getOrDefault(handler.getType(), new LinkedList<>());
            bindHandlers.add(handler);
            PROPERTY_HANDLERS.put(handler.getType(), bindHandlers);
        }
    }

    /**
     * 转换对应属性
     */
    public <T> T covertValue(String propName, String propValue, Class<T> type) {
        var handlers = PROPERTY_HANDLERS.getOrDefault(type, EMPTY_COLLECTION);
        PropertyConvertException exception = null;
        for (PropertyHandler<?> handler : handlers) {
            try {
                // 执行转换,  若抛出异常则尝试使用后续转换器执行
                return (T) handler.convert(propName, propValue);
            } catch (PropertyConvertException e) {
                exception = e;
            }
        }
        //  若所有转换都未转换成功, 则抛出异常
        if (exception == null) {
            exception = new PropertyConvertException("PropertyProcessor无法转换[" + type.getTypeName() + "], 对应属性名称:[" + propName + "], 属性值:[" + propValue + "]");
        }
        throw exception;
    }

}
