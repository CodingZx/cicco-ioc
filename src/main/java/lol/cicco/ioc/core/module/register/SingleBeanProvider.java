package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.aop.AopProcessor;

class SingleBeanProvider implements BeanProvider {

    private final Class<?> originCls;
    private final AopProcessor aopProcessor;

    private Object singleObj;

    SingleBeanProvider(Class<?> originCls, AopProcessor aopProcessor) {
        this.originCls = originCls;
        this.aopProcessor = aopProcessor;
    }

    @Override
    public Class<?> beanType() {
        return originCls;
    }

    @Override
    public Object getObject() {
        if(singleObj == null) {
            synchronized (originCls) {
                Object object = aopProcessor.beanEnhance(originCls);
                this.singleObj = object;
                return object;
            }
        }
        return singleObj;
    }
}
