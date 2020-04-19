package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.core.module.aop.InterceptorRegistry;
import lol.cicco.ioc.core.module.beans.BeanProvider;

class SingleBeanProvider implements BeanProvider {

    private final Class<?> originCls;
    private final InterceptorRegistry registry;

    private Object singleObj;

    SingleBeanProvider(Class<?> originCls, InterceptorRegistry registry) {
        this.originCls = originCls;
        this.registry = registry;
    }

    @Override
    public Class<?> beanType() {
        return originCls;
    }

    @Override
    public Object getObject() {
        if(singleObj == null) {
            synchronized (originCls) {
                Object object = JavassistProxy.proxyEnhance(originCls, registry);
                this.singleObj = object;
                return object;
            }
        }
        return singleObj;
    }
}
