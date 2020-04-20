package lol.cicco.ioc.core.module.register;

import javassist.util.proxy.ProxyFactory;
import lol.cicco.ioc.core.module.aop.Interceptor;
import lol.cicco.ioc.core.module.aop.InterceptorRegistry;
import lol.cicco.ioc.core.module.aop.JoinPointImpl;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

final class JavassistProxy {

    @SneakyThrows
    static Object proxyEnhance(Class<?> superCls, InterceptorRegistry registry) {
        Map<Method, Annotation[]> methodInfo = filterMethods(superCls);

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(superCls);
        factory.setUseCache(true);
        factory.setFilter(m -> methodInfo.get(m) != null);
        return factory.create(new Class<?>[]{}, new Object[]{}, (self, thisMethod, proceed, args) -> {
            Annotation[] methodAnnotations = methodInfo.get(thisMethod);
            if (methodAnnotations == null || methodAnnotations.length == 0) {
                return proceed.invoke(self, args);
            }

            List<Interceptor<?>> hasInterceptors = Arrays.stream(methodAnnotations).map(f -> registry.getInterceptor(f.annotationType())).filter(Objects::nonNull).collect(Collectors.toList());

            if (hasInterceptors.isEmpty()) {
                return proceed.invoke(self, args);
            }
            JoinPointImpl point = new JoinPointImpl(self, thisMethod, args);
            try {
                for (Interceptor<?> interceptor : hasInterceptors) {
                    interceptor.before(point);
                }

                Object result = proceed.invoke(self, args);

                point.setReturnValue(result);
                for (Interceptor<?> interceptor : hasInterceptors) {
                    interceptor.after(point);
                }

                return result;
            } catch (Exception e) {
                point.setThrowable(e);
                for (Interceptor<?> interceptor : hasInterceptors) {
                    interceptor.throwException(point);
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
