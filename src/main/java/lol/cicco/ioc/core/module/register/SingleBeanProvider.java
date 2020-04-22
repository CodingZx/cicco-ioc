package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.core.module.aop.InterceptorRegistry;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.util.Arrays;

class SingleBeanProvider extends AbstractBeanProvider {

    private final BeanRegistry beanRegistry;
    private final Object singleObj;

    SingleBeanProvider(InterceptorRegistry interceptorRegistry, BeanRegistry beanRegistry, AnalyzeBeanDefine beanDefine) {
        super(beanDefine.getBeanType(), interceptorRegistry);
        this.beanRegistry = beanRegistry;
        // 创建实例
        this.singleObj = createProxy(false, beanDefine.getParameterTypes(), getConstructorParams(beanDefine.getParameterTypes(), beanDefine.getParameterAnnotations()));
    }

    @Override
    @SneakyThrows
    public Object getObject() {
        return singleObj;
    }

    private Object[] getConstructorParams(Class<?>[] parameterTypes, Annotation[][] parameterAnnotations) {
        Object[] constructorParams;
        if (parameterTypes.length == 0) { //默认构造方法
            constructorParams = new Object[]{};
        } else {
            constructorParams = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> constructorType = parameterTypes[i];
                Annotation[] paramAnnotations = parameterAnnotations[i];

                Inject injectParam = (Inject) Arrays.stream(paramAnnotations).filter(a -> a.annotationType().equals(Inject.class)).findFirst().orElse(null);
                boolean required;
                BeanProvider provider;
                if (injectParam == null) {
                    provider = beanRegistry.getNullableBean(constructorType);
                    required = true;
                } else {
                    required = injectParam.required();
                    if (injectParam.byName().trim().equals("")) {
                        provider = beanRegistry.getNullableBean(constructorType);
                    } else {
                        provider = beanRegistry.getNullableBean(injectParam.byName().trim());
                    }
                }
                if (provider == null && required) {
                    throw new RegisterException("Class[" + originCls.getName() + "] 未找到注入类型[" + parameterTypes[i].getName() + "], 请检查构造参数是否正确..");
                }
                parameterTypes[i] = constructorType;
                constructorParams[i] = provider == null ? null : provider.getObject();
            }
        }
        return constructorParams;
    }

}
