package lol.cicco.ioc.core.module.aop;

import java.lang.annotation.Annotation;

public interface InterceptorRegistry {

    /**
     * 注册拦截器
     */
    void register(AnnotationInterceptor<?> annotationInterceptor);

    /**
     * 根据注解获得对应拦截器
     */
    AnnotationInterceptor<?> getInterceptor(Class<? extends Annotation> annotation);
}
