package lol.cicco.ioc.core.aop;

import lol.cicco.ioc.core.BeanDefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AopProcessor {
    private final Map<String, Interceptor> interceptorMap = new LinkedHashMap<>();

    public void register(Map<Class<? extends Annotation>, Interceptor> interceptors) {
        for (Class<? extends Annotation> cls : interceptors.keySet()) {
            interceptorMap.put(cls.getName(), interceptors.get(cls));
        }
    }

    public Interceptor getInterceptor(String name) {
        return interceptorMap.get(name);
    }

    /*
    public Object beanEnhance2(BeanDefinition definition) {
        Map<Method, List<String>> interceptors = new LinkedHashMap<>();
        Class<?> beanType = definition.getSelfType();

        for(Method method : beanType.getDeclaredMethods()) {
            for(Annotation annotation : method.getDeclaredAnnotations()) {
                Interceptor registerInter = interceptorMap.get(annotation.annotationType().getName());
                if(registerInter != null) {
                    List<String> hasInters = interceptors.getOrDefault(method, new LinkedList<>());
                    hasInters.add(annotation.annotationType().getName());
                    interceptors.put(method, hasInters);
                }
            }
        }
        BeanProxy beanProxy = (BeanProxy) JavassistEnhance2.beanEnhance(beanType.getName(), interceptors);
        beanProxy.setProcessor(this);
        for(Method method : interceptors.keySet()) {
            beanProxy.putMethod(method.toGenericString(), method);
        }
        return beanProxy;
    }
    */

    public Object beanEnhance(BeanDefinition definition) {
        Map<Method, List<Interceptor>> interceptors = new LinkedHashMap<>();
        Class<?> beanType = definition.getSelfType();

        for (Method method : beanType.getDeclaredMethods()) {
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                Interceptor registerInter = interceptorMap.get(annotation.annotationType().getName());
                if (registerInter != null) {
                    List<Interceptor> hasInters = interceptors.getOrDefault(method, new LinkedList<>());
                    hasInters.add(registerInter);
                    interceptors.put(method, hasInters);
                }
            }
        }
        return JavassistEnhance.proxyEnhance(beanType, interceptors);
    }
}
