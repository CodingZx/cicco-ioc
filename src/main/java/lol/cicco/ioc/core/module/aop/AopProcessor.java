package lol.cicco.ioc.core.module.aop;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class AopProcessor implements InterceptorRegistry{
    private final Map<String, Interceptor<?>> interceptorMap = new LinkedHashMap<>();

    @Override
    public void register(Interceptor<?> interceptor) {
        log.debug("aopProcessor register [{}]", interceptor.getAnnotation().getName());
        interceptorMap.put(interceptor.getAnnotation().getName(), interceptor);
    }

    @Override
    public Interceptor<?> getInterceptor(Class<? extends Annotation> annotation) {
        return interceptorMap.get(annotation.getName());
    }

    public Object beanEnhance(Class<?> superCls) {
        return JavassistProxy.proxyEnhance(superCls, this);
    }
}
