package lol.cicco.ioc.core.module.register;

import javassist.util.proxy.ProxyFactory;
import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.core.module.aop.AnnotationInterceptor;
import lol.cicco.ioc.core.module.aop.InterceptorRegistry;
import lol.cicco.ioc.core.module.aop.JoinPointImpl;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractBeanProvider implements BeanProvider {

    protected final Class<?> originCls;
    protected final BeanRegistry beanRegistry;
    protected final InterceptorRegistry interceptorRegistry;

    protected AbstractBeanProvider(Class<?> originCls, BeanRegistry beanRegistry, InterceptorRegistry interceptorRegistry) {
        this.originCls = originCls;
        this.beanRegistry = beanRegistry;
        this.interceptorRegistry = interceptorRegistry;
    }

    @Override
    public Class<?> beanType() {
        return originCls;
    }


    @SneakyThrows
    protected Object createProxy() {
        Map<Method, Annotation[]> methodInfo = filterBeanTypeMethods();

        ProxyFactory factory = new ProxyFactory();
        Class<?> superCls = superClass();
        if(superCls != null) {
            factory.setSuperclass(superCls);
        }
        Class<?>[] interfaceCls = interfaceClass();
        if(interfaceCls != null && interfaceCls.length != 0) {
            factory.setInterfaces(interfaceCls);
        }

        factory.setUseCache(false); // 不需要缓存..

        Class<?>[] parameterTypes = getProxyParameterTypes();
        // 生成代理
        return factory.create(parameterTypes, getParams(parameterTypes, getProxyParameterAnnotations()), (self, thisMethod, proceed, args) -> {
            Annotation[] methodAnnotations = methodInfo.get(thisMethod);
            if (methodAnnotations == null || methodAnnotations.length == 0) {
                return invoke(self, thisMethod, proceed, args);
            }
            // 获得方法所有的拦截器
            List<AnnotationInterceptor<?>> hasAnnotationInterceptors = Arrays.stream(methodAnnotations).map(f -> interceptorRegistry.getInterceptor(f.annotationType())).filter(Objects::nonNull).collect(Collectors.toList());

            if (hasAnnotationInterceptors.isEmpty()) {
                // 没有拦截器 直接继续执行
                return invoke(self, thisMethod, proceed, args);
            }
            JoinPointImpl point = new JoinPointImpl(self, thisMethod, args);
            try {
                // 执行前置拦截器
                for (AnnotationInterceptor<?> annotationInterceptor : hasAnnotationInterceptors) {
                    annotationInterceptor.before(point);
                }

                Object result = invoke(self, thisMethod, proceed, args);

                // 获得执行结果
                point.setReturnValue(result);
                // 执行后续拦截器
                for (AnnotationInterceptor<?> annotationInterceptor : hasAnnotationInterceptors) {
                    annotationInterceptor.after(point);
                }
                return result;
            } catch (Exception e) {
                point.setThrowable(e);
                // 执行异常拦截器
                for (AnnotationInterceptor<?> annotationInterceptor : hasAnnotationInterceptors) {
                    annotationInterceptor.throwException(point);
                }
                throw e;
            }
        });
    }

    protected Object[] getParams(Class<?>[] parameterTypes, Annotation[][] parameterAnnotations) {
        Object[] params;
        if (parameterTypes.length == 0) { //默认构造方法
            params = new Object[]{};
        } else {
            params = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> constructorType = parameterTypes[i];

                Inject injectParam = parameterAnnotations[i] == null ? null : (Inject) Arrays.stream(parameterAnnotations[i]).filter(a -> a.annotationType().equals(Inject.class)).findFirst().orElse(null);
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
                params[i] = provider == null ? null : provider.getObject();
            }
        }
        return params;
    }

    public abstract Class<?> superClass();

    public abstract Class<?>[] interfaceClass();

    public abstract Object invoke(Object self, Method thisMethod, Method proceed, Object[] args);

    public abstract Class<?>[] getProxyParameterTypes();

    public abstract Annotation[][] getProxyParameterAnnotations();

    public abstract Map<Method, Annotation[]> filterBeanTypeMethods();
}
