package lol.cicco.ioc.core.module.aop;

import javassist.util.proxy.ProxyFactory;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class JavassistProxy {

    @SneakyThrows
    static Object proxyEnhance(Class<?> superCls, AopProcessor processor) {
        Map<Method, Annotation[]> methodInfo = filterMethods(superCls);

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(superCls);
        factory.setUseCache(true);
        factory.setFilter(m -> methodInfo.get(m) != null);
        return factory.create(new Class<?>[]{}, new Object[]{}, (self, thisMethod, proceed, args) -> {
            Annotation[] methodAnnotations = methodInfo.get(thisMethod);
            if(methodAnnotations == null || methodAnnotations.length == 0) {
                return thisMethod.invoke(self, args);
            }

            List<Interceptor<?>> hasInterceptors = Arrays.stream(methodAnnotations).map(f -> processor.getInterceptor(f.annotationType())).collect(Collectors.toList());

            JoinPointImpl point = new JoinPointImpl(self, thisMethod, args);
            for (Interceptor<?> interceptor : hasInterceptors) {
                interceptor.before(point);
            }

            Object result = proceed.invoke(self, args);

            point.setReturnValue(result);
            for (Interceptor<?> interceptor : hasInterceptors) {
                interceptor.after(point);
            }
            return result;
        });
    }

    private static Map<Method, Annotation[]> filterMethods(Class<?> superCls) {
        Map<Method, Annotation[]> methodMap = new LinkedHashMap<>();
        for(Method method : superCls.getDeclaredMethods()) {
            Annotation[] annotations = method.getAnnotations();
            if(annotations == null || annotations.length == 0) {
                continue;
            }
            methodMap.put(method, annotations);
        }
        return methodMap;
    }
}
