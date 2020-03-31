package lol.cicco.ioc.core;

import lol.cicco.ioc.core.exception.BeanInitializeException;

public final class IOC {

    private IOC() {
        throw new IllegalAccessError();
    }
    private static IOCContainer iocContainer;

    static void initializeDone(Initialize initialize) {
        synchronized (IOC.class) {
            if(iocContainer != null) {
                throw new BeanInitializeException("不能重复初始化IOC...");
            }
            // 初始化Container...
            IOC.iocContainer = IOCContainer.create(initialize);

        }
    }

    public static Initialize initialize() {
        return new Initialize();
    }

    public static <T> T getBeanByType(Class<T> beanCls){
        return iocContainer.getBeanByType(beanCls);
    }

    public static String getProperty(String key, String defaultValue) {
        return iocContainer.getProperty(key, defaultValue);
    }
}
