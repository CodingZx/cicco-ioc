package lol.cicco.ioc.core;

import lol.cicco.ioc.core.exception.BeanDefinitionStoreException;
import lol.cicco.ioc.core.exception.BeanInitializeException;

public final class IOC {

    private IOC() {
        throw new IllegalAccessError();
    }
    private static CiccoContainer ciccoContainer;

    static void initializeDone(Initialize initialize) {
        synchronized (IOC.class) {
            if(ciccoContainer != null) {
                throw new BeanInitializeException("不能重复初始化IOC...");
            }

            // 初始化Container...
            IOC.ciccoContainer = CiccoContainer.create(initialize);
        }
    }

    public static Initialize initialize() {
        return new Initialize();
    }

    public static <T> T getBeanByType(Class<T> beanCls){
        checkContainer();
        return ciccoContainer.getBeanByType(beanCls);
    }

    public static String getProperty(String key, String defaultValue) {
        checkContainer();
        return ciccoContainer.getProperty(key, defaultValue);
    }

    private static void checkContainer(){
        if(ciccoContainer == null) {
            throw new BeanDefinitionStoreException("IOC容器未初始化..");
        }
    }
}
