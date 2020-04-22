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

class SingleBeanProvider implements BeanProvider {

    private final Class<?> originCls;
    private final InterceptorRegistry interceptorRegistry;
    private final BeanRegistry beanRegistry;
    private final AnalyzeBeanDefine beanDefine;

    private final Object targetObject;

    SingleBeanProvider(InterceptorRegistry interceptorRegistry, BeanRegistry beanRegistry, AnalyzeBeanDefine beanDefine) {
        this.originCls = beanDefine.getBeanType();
        this.interceptorRegistry = interceptorRegistry;
        this.beanRegistry = beanRegistry;
        this.beanDefine = beanDefine;

        // 创建实例
        this.targetObject = createProxy();
    }

    @Override
    public Class<?> beanType() {
        return originCls;
    }

    @Override
    @SneakyThrows
    public Object getObject() {
        return targetObject;
    }

    private Object[] getConstructorParams() {
        Class<?>[] parameterTypes = beanDefine.getParameterTypes();
        Annotation[][] parameterAnnotations = beanDefine.getParameterAnnotations();

        Object[] constructorParams;
        if (parameterTypes.length == 0) { //默认构造方法
            constructorParams = new Object[]{};
        } else {
            constructorParams = new Object[parameterTypes.length];
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
                constructorParams[i] = provider == null ? null : provider.getObject();
            }
        }
        return constructorParams;
    }


    @SneakyThrows
    protected Object createProxy() {
        Map<Method, Annotation[]> methodInfo = filterMethods(originCls);

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(originCls);
        factory.setUseCache(false); // 不需要缓存..
        factory.setFilter(m -> methodInfo.get(m) != null);

        // 生成代理
        return factory.create(beanDefine.getParameterTypes(), getConstructorParams(), (self, thisMethod, proceed, args) -> {
            Annotation[] methodAnnotations = methodInfo.get(thisMethod);
            if (methodAnnotations == null || methodAnnotations.length == 0) {
                return proceed.invoke(self, args);
            }
            // 获得方法所有的拦截器
            List<AnnotationInterceptor<?>> hasAnnotationInterceptors = Arrays.stream(methodAnnotations).map(f -> interceptorRegistry.getInterceptor(f.annotationType())).filter(Objects::nonNull).collect(Collectors.toList());

            if (hasAnnotationInterceptors.isEmpty()) {
                // 没有拦截器 直接继续执行
                return proceed.invoke(self, args);
            }
            JoinPointImpl point = new JoinPointImpl(self, thisMethod, args);
            try {
                // 执行前置拦截器
                for (AnnotationInterceptor<?> annotationInterceptor : hasAnnotationInterceptors) {
                    annotationInterceptor.before(point);
                }

                Object result = proceed.invoke(self, args);

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

    private Map<Method, Annotation[]> filterMethods(Class<?> superCls) {
        Map<Method, Annotation[]> methodMap = new LinkedHashMap<>();
        for (Method method : superCls.getDeclaredMethods()) {
            Annotation[] annotations = method.getAnnotations();
            if (annotations == null || annotations.length == 0) {
                continue;
            }
            methodMap.put(method, annotations);
        }
        return methodMap;
    }
}
