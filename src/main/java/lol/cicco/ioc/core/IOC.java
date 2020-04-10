package lol.cicco.ioc.core;

import lol.cicco.ioc.core.binder.PropertyProcessor;
import lol.cicco.ioc.core.exception.BeanDefinitionStoreException;
import lol.cicco.ioc.core.exception.BeanInitializeException;

public final class IOC {

    private IOC() {
        throw new IllegalAccessError();
    }

    private static CiccoContainer ciccoContainer;

    /**
     * 初始化IOC
     */
    static void initializeDone(Initialize initialize) {
        synchronized (IOC.class) {
            if (ciccoContainer != null) {
                throw new BeanInitializeException("不能重复初始化IOC...");
            }

            // 初始化Container...
            IOC.ciccoContainer = CiccoContainer.create(initialize);
        }
    }

    /**
     * 初始化IOC
     */
    public static Initialize initialize() {
        return new Initialize();
    }

    /**
     * 根据Class类型获得IOC对应实例
     */
    public static <T> T getBeanByType(Class<T> beanCls) {
        checkContainer();
        return ciccoContainer.getBeanByType(beanCls);
    }

    /**
     * 根据Bean名称获得IOC对应实例
     */
    public static <T> T getBeanByName(String beanName) {
        checkContainer();
        return ciccoContainer.getBeanByName(beanName);
    }

    /**
     * 获得IOC中属性值
     */
    public static String getProperty(String propertyName, String defaultValue) {
        checkContainer();
        return ciccoContainer.getProperty(propertyName, defaultValue);
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
        checkContainer();
        return ciccoContainer.getProperty(propertyName, cls);
    }

    // 检查当前container是否已初始化
    private static void checkContainer() {
        if (ciccoContainer == null) {
            throw new BeanDefinitionStoreException("IOC容器未初始化..");
        }
    }
}
