package lol.cicco.ioc.core.module.aop;

import java.lang.annotation.Annotation;

public interface InterceptorRegistry {

    void register(Interceptor<?> interceptor);

    Interceptor<?> getInterceptor(Class<? extends Annotation> annotation);
}
