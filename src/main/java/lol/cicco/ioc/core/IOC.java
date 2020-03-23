package lol.cicco.ioc.core;

import lol.cicco.ioc.core.exception.BeanInitializeException;

public class IOC {

    private IOC() {
        throw new IllegalAccessError();
    }

    private static BeanContainer container;

    static void initializeDone(BeanContainer container) {
        synchronized (IOC.class) {
            if(IOC.container != null) {
                throw new BeanInitializeException("不能重复初始化IOC...");
            }
            IOC.container = container;
        }
    }

    public static Initialize initialize() {
        return new Initialize();
    }

    public static <T> T getBeanByType(Class<T> beanCls){
        return container.getBeanByType(beanCls);
    }
}
