package lol.cicco.ioc.core.module.property;

import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lol.cicco.ioc.core.IOC;
import lol.cicco.ioc.core.Initialize;
import lol.cicco.ioc.core.module.beans.BeanModule;
import lol.cicco.ioc.core.module.register.RegisterModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public class PropertyModule implements CiccoModule<PropertyRegistry>, PropertyRegistry {
    public static final String PROPERTY_MODULE_NAME = "_propertyModule";

    // 属性转换器存在Map, 允许同一种类型对应多个转换器
    private final Map<Type, Collection<PropertyHandler<?>>> PROPERTY_HANDLERS = new LinkedHashMap<>();

    private final PropertyListenerRegistry propertyListenerRegistry = new PropertyListenerRegistry();
    private final Map<String, String> propValues = new LinkedHashMap<>(); // 加载的属性值

    @Override
    public void initModule(CiccoContext context) {
        // 提供注册基本类型及相关JDK中类型的转换器
        registerHandler(NumberPropertyHandler.create());
        registerHandler(StringPropertyHandler.create());
        registerHandler(Java8TimePropertyHandler.create());
        registerHandler(UUIDPropertyHandler.create());


        Initialize initialize = context.getInitialize();
        loadProperties(initialize.getLoadPropertyFiles());

        log.debug("init property module....");
    }

    @Override
    public String getModuleName() {
        return PROPERTY_MODULE_NAME;
    }

    @Override
    public PropertyRegistry getModuleProcessor() {
        return this;
    }

    @Override
    public List<String> dependModule() {
        return Collections.singletonList(BeanModule.BEAN_MODULE_NAME);
    }

    @Override
    public List<String> afterModule() {
        return Collections.singletonList(RegisterModule.REGISTER_MODULE_NAME);
    }

    @SneakyThrows
    private void loadProperties(Set<String> loadProperties) {
        // 加载对应配置
        for (String propertyName : loadProperties) {
            var inputStream = IOC.class.getResourceAsStream(propertyName);
            if (inputStream == null) {
                log.warn("[{}] 未找到对应文件....", propertyName);
                continue;
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            for (var key : properties.keySet()) {
                var value = properties.getProperty(key.toString());
                setProperty(key.toString(), value);
            }
        }
    }

    /**
     * 注册自定义属性处理器
     */
    @Override
    public void registerHandler(PropertyHandler<?> handler) {
        registerHandler(Collections.singleton(handler));
    }

    /**
     * 注册自定义属性处理器
     */
    protected void registerHandler(Collection<PropertyHandler<?>> handlers) {
        for (PropertyHandler<?> handler : handlers) {
            log.debug("PropertyHandler注册类型[{}], 对应处理器[{}]", handler.getType().getTypeName(), handler.getClass().toString());
            var bindHandlers = PROPERTY_HANDLERS.getOrDefault(handler.getType(), new LinkedList<>());
            bindHandlers.add(handler);
            PROPERTY_HANDLERS.put(handler.getType(), bindHandlers);
        }
    }

    @Override
    public void setProperty(String propertyName, String propertyValue) {
        synchronized (propValues) {
            propValues.put(propertyName, propertyValue);
            propertyListenerRegistry.onChange(propertyName);
        }
    }

    /**
     * 转换对应属性
     */
    @Override
    public <T> T convertValue(String propName, String defaultValue, Class<T> type) {
        var propValue = getProperty(propName, defaultValue);
        var handlers = PROPERTY_HANDLERS.getOrDefault(type, Collections.emptyList());
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

    /**
     * 根据提供属性名称获得对应属性值
     */
    @Override
    public String getProperty(String propertyName, String defaultValue) {
        return propValues.getOrDefault(propertyName, defaultValue);
    }

    /**
     * 移除属性
     */
    @Override
    public void removeProperty(String propertyName) {
        synchronized (propValues) {
            propValues.remove(propertyName);
            propertyListenerRegistry.onChange(propertyName);
        }
    }

    /**
     * 注册属性监听器
     */
    @Override
    public void registerPropertyListener(PropertyChangeListener listener) {
        propertyListenerRegistry.register(listener);
    }

    /**
     * 移除属性监听器
     */
    @Override
    public void removePropertyListener(String propertyName, String listenerSign) {
        propertyListenerRegistry.removeListener(propertyName, listenerSign);
    }

}
