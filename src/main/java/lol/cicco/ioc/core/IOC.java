package lol.cicco.ioc.core;

import lol.cicco.ioc.core.exception.BeanInitializeException;

import java.util.Set;

public final class IOC {

    private IOC() {
        throw new IllegalAccessError();
    }

    private static BeanContainer container;

    static void initializeDone(Set<BeanDefinition> beanDefinitions) {
        synchronized (IOC.class) {
            if(IOC.container != null) {
                throw new BeanInitializeException("不能重复初始化IOC...");
            }
            // 初始化Container...
            IOC.container = BeanContainer.create(beanDefinitions);
        }
    }

    public static Initialize initialize() {
        return new Initialize();
    }

    public static <T> T getBeanByType(Class<T> beanCls){
        return container.getBeanByType(beanCls);
    }


}
