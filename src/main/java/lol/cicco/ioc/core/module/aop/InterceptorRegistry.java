package lol.cicco.ioc.core.module.aop;

import java.lang.annotation.Annotation;

public interface InterceptorRegistry {

    /**
     * 注册拦截器
     */
    void register(Interceptor<?> interceptor);

    /**
     * 根据注解获得对应拦截器
     */
    Interceptor<?> getInterceptor(Class<? extends Annotation> annotation);

    /**
     * 创建拦截器代理
     */
    Object createProxy(Class<?> originCls);
}
