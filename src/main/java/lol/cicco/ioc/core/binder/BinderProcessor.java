package lol.cicco.ioc.core.binder;

import lol.cicco.ioc.core.exception.PropertyBindException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public final class BinderProcessor {
    private static final Map<Type, List<BindHandler<?>>> bindHandler = new LinkedHashMap<>();

    private static final BinderProcessor OBJECT = new BinderProcessor();

    private BinderProcessor() {
        registerHandler(new NumberBindHandler(Integer.TYPE));
        registerHandler(new NumberBindHandler(Short.TYPE));
        registerHandler(new NumberBindHandler(Byte.TYPE));
        registerHandler(new NumberBindHandler(Long.TYPE));
        registerHandler(new NumberBindHandler(Float.TYPE));
        registerHandler(new NumberBindHandler(Double.TYPE));
        registerHandler(new NumberBindHandler(AtomicInteger.class));
        registerHandler(new NumberBindHandler(AtomicLong.class));
        registerHandler(new NumberBindHandler(BigDecimal.class));
        registerHandler(new NumberBindHandler(BigInteger.class));
        registerHandler(new StringBindHandler());
        registerHandler(new Java8TimeBindHandler(LocalDate.class));
        registerHandler(new Java8TimeBindHandler(LocalDateTime.class));
        registerHandler(new Java8TimeBindHandler(LocalTime.class));
        registerHandler(new Java8TimeBindHandler(Year.class));
        registerHandler(new Java8TimeBindHandler(Month.class));
        registerHandler(new Java8TimeBindHandler(MonthDay.class));
        registerHandler(new Java8TimeBindHandler(YearMonth.class));
        registerHandler(new UUIDBindHandler());
    }

    public static BinderProcessor getInstance() {
        return OBJECT;
    }

    public void registerHandler(BindHandler<?> handler) {
        log.debug("Binder处理器注册类型[{}], 对应处理器[{}]", handler.bindType().getTypeName(), handler.getClass().toString());
        var handlers = bindHandler.getOrDefault(handler.bindType(), new LinkedList<>());
        handlers.add(handler);
        bindHandler.put(handler.bindType(), handlers);
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
