package lol.cicco.ioc.core.module.register;

import javassist.util.proxy.ProxyFactory;
import lol.cicco.ioc.core.module.aop.AnnotationInterceptor;
import lol.cicco.ioc.core.module.aop.InterceptorRegistry;
import lol.cicco.ioc.core.module.aop.JoinPointImpl;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

abstract class AbstractBeanProvider implements BeanProvider {

    protected final Class<?> originCls;
    protected final InterceptorRegistry interceptorRegistry;

    AbstractBeanProvider(Class<?> originCls, InterceptorRegistry interceptorRegistry) {
        this.originCls = originCls;
        this.interceptorRegistry = interceptorRegistry;
    }

    @Override
    public Class<?> beanType() {
        return originCls;
    }

    @SneakyThrows
    protected Object createProxy(boolean useProxyCache, Class<?>[] factoryParamTypes, Object[] factoryArgs) {
        Map<Method, Annotation[]> methodInfo = filterMethods(originCls);

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(originCls);
        factory.setUseCache(useProxyCache); // 不需要缓存..
        factory.setFilter(m -> methodInfo.get(m) != null);

        // 生成代理
        return factory.create(factoryParamTypes, factoryArgs, (self, thisMethod, proceed, args) -> {
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

    private static Map<Method, Annotation[]> filterMethods(Class<?> superCls) {
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
