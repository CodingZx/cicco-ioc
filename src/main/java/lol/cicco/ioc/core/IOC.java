package lol.cicco.ioc.core;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.module.beans.BeanModule;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lol.cicco.ioc.core.module.beans.BeanStoreException;
import lol.cicco.ioc.core.module.property.PropertyModule;
import lol.cicco.ioc.core.module.property.PropertyRegistry;
import lol.cicco.ioc.core.module.register.RegisterException;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;

public class IOC {

    private IOC() {
        throw new IllegalAccessError();
    }

    private static CiccoContext context;

    /**
     * 初始化IOC
     */
    static void initializeDone(Initialize initialize) {
        synchronized (IOC.class) {
            if (context != null) {
                throw new RegisterException("不能重复初始化IOC...");
            }
            IOC.context = new CiccoContext(initialize);
        }
    }

    protected CiccoContext getContext() {
        return context;
    }

    /**
     * 初始化IOC
     */
    public static Initialize initialize() {
        return initialize(DefaultInitialize.class);
    }

    @SneakyThrows
    public static <T extends Initialize> T initialize(Class<T> initializeClass) {
        Constructor<T> constructor;
        try {
            constructor = initializeClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new InitializeException("初始化失败, Initialize实现类必须包含默认构造方法..");
        }
        return constructor.newInstance();
    }

    /**
     * 根据Class类型获得IOC对应实例
     */
    public static <T> T getBeanByType(Class<T> beanCls) {
        checkProcessor();
        BeanRegistry beanRegistry = ((BeanModule) context.getModule(BeanModule.BEAN_MODULE_NAME)).getModuleProcessor();
        BeanProvider provider = beanRegistry.getNullableBean(beanCls);
        if (provider == null) {
            throw new BeanNotFountException("[" + beanCls.toString() + "] 未注册至IOC, 请检查[" + Registration.class + "]注解与初始化配置.");
        }
        return (T) provider.getObject();
    }

    /**
     * 根据Bean名称获得IOC对应实例
     */
    public static <T> T getBeanByName(String beanName) {
        checkProcessor();
        BeanRegistry beanRegistry = ((BeanModule) context.getModule(BeanModule.BEAN_MODULE_NAME)).getModuleProcessor();
        BeanProvider provider = beanRegistry.getNullableBean(beanName);
        if (provider == null) {
            throw new BeanNotFountException("[" + beanName + "] 未注册至IOC, 请检查[" + Registration.class + "]注解与初始化配置.");
        }
        return (T) provider.getObject();
    }

    /**
     * 获得IOC中属性值
     */
    public static String getProperty(String propertyName, String defaultValue) {
        checkProcessor();
        PropertyRegistry propertyRegistry = ((PropertyModule) context.getModule(PropertyModule.PROPERTY_MODULE_NAME)).getModuleProcessor();
        return propertyRegistry.getProperty(propertyName, defaultValue);
    }

    /**
     * 获得IOC中属性值
     */
    public static String getProperty(String propertyName) {
        return getProperty(propertyName, (String) null);
    }

    /**
     * 获得IOC中属性值
     */
    public static <T> T getProperty(String propertyName, Class<T> cls) {
        checkProcessor();
        PropertyRegistry propertyRegistry = ((PropertyModule) context.getModule(PropertyModule.PROPERTY_MODULE_NAME)).getModuleProcessor();
        return propertyRegistry.convertValue(propertyName, null, cls);
    }

    /**
     * 设置属性
     */
    public static void setProperty(String propertyName, String propertyValue) {
        checkProcessor();
        PropertyRegistry propertyRegistry = ((PropertyModule) context.getModule(PropertyModule.PROPERTY_MODULE_NAME)).getModuleProcessor();
        propertyRegistry.setProperty(propertyName, propertyValue);
    }

    /**
     * 移除属性
     */
    public static void removeProperty(String propertyName) {
        checkProcessor();
        PropertyRegistry propertyRegistry = ((PropertyModule) context.getModule(PropertyModule.PROPERTY_MODULE_NAME)).getModuleProcessor();
        propertyRegistry.removeProperty(propertyName);
    }

    // 检查当前Processor是否已初始化
    private static void checkProcessor() {
        if (context == null) {
            throw new BeanStoreException("IOC容器未初始化..");
        }
    }
}
