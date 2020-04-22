package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.core.module.aop.InterceptorRegistry;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;

class SingleBeanProvider extends AbstractBeanProvider {

    private final Class<?> originCls;
    private final BeanRegistry beanRegistry;
    private final Constructor<?> beanConstructor;

    private final Object singleObj;

    SingleBeanProvider(Class<?> originCls, InterceptorRegistry interceptorRegistry, BeanRegistry beanRegistry, Constructor<?> beanConstructor) {
        super(originCls, interceptorRegistry);
        this.originCls = originCls;
        this.beanRegistry = beanRegistry;
        this.beanConstructor = beanConstructor;
        // 创建实例
        this.singleObj = createProxy(beanConstructor.getParameterTypes(), getConstructorParams());
    }

    @Override
    @SneakyThrows
    public Object getObject() {
        return singleObj;
    }

    private Object[] getConstructorParams(){
        Constructor<?> constructor = beanConstructor;

        Object[] constructorParams;
        if (constructor.getParameterTypes().length == 0) { //默认构造方法
            constructorParams = new Object[]{};
        } else {
            Annotation[][] annotations = constructor.getParameterAnnotations();

            Class<?>[] constructorTypes = constructor.getParameterTypes();
            constructorParams = new Object[constructorTypes.length];
            for (int i = 0; i < constructorTypes.length; i++) {
                Class<?> constructorType = constructorTypes[i];
                Annotation[] paramAnnotations = annotations[i];

                Inject injectParam = (Inject) Arrays.stream(paramAnnotations).filter(a -> a.annotationType().equals(Inject.class)).findFirst().orElse(null);
                boolean required;
                lol.cicco.ioc.core.module.beans.BeanProvider provider;
                if(injectParam == null) {
                    provider = beanRegistry.getNullableBean(constructorType);
                    required = true;
                } else {
                    required = injectParam.required();
                    if(injectParam.byName().trim().equals("")) {
                        provider = beanRegistry.getNullableBean(constructorType);
                    } else {
                        provider = beanRegistry.getNullableBean(injectParam.byName().trim());
                    }
                }
                if (provider == null && required) {
                    throw new RegisterException("Class[" + originCls.getName() + "] 未找到注入类型[" + constructorTypes[i].getName() + "], 请检查构造参数是否正确..");
                }
                constructorTypes[i] = constructorType;
                constructorParams[i] = provider == null ? null : provider.getObject();
            }
        }
        return constructorParams;
    }

}
