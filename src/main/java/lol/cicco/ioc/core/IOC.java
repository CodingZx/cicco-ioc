package lol.cicco.ioc.core;

import lol.cicco.ioc.core.binder.BindHandler;
import lol.cicco.ioc.core.binder.BinderProcessor;
import lol.cicco.ioc.core.exception.BeanDefinitionStoreException;
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

            BinderProcessor binder = BinderProcessor.getInstance();
            for(BindHandler<?> handler : initialize.getBindHandlers()){
                binder.registerHandler(handler);
            }
        }
    }

    public static Initialize initialize() {
        return new Initialize();
    }

    public static <T> T getBeanByType(Class<T> beanCls){
        checkContainer();
        return iocContainer.getBeanByType(beanCls);
    }

    public static String getProperty(String key, String defaultValue) {
        checkContainer();
        return iocContainer.getProperty(key, defaultValue);
    }

    private static void checkContainer(){
        if(iocContainer == null) {
            throw new BeanDefinitionStoreException("IOC容器未初始化..");
        }
    }
}
