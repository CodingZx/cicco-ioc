package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.core.module.aop.AopModule;
import lol.cicco.ioc.core.module.beans.BeanProvider;

class SingleBeanProvider implements BeanProvider {

    private final Class<?> originCls;
    private final AopModule aopModule;

    private Object singleObj;

    SingleBeanProvider(Class<?> originCls, AopModule aopModule) {
        this.originCls = originCls;
        this.aopModule = aopModule;
    }

    @Override
    public Class<?> beanType() {
        return originCls;
    }

    @Override
    public Object getObject() {
        if(singleObj == null) {
            synchronized (originCls) {
                Object object = aopModule.getModuleProcessor().beanEnhance(originCls);
                this.singleObj = object;
                return object;
            }
        }
        return singleObj;
    }
}
