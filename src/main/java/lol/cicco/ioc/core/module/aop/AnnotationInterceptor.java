package lol.cicco.ioc.core.module.aop;

import java.lang.annotation.Annotation;

/**
 * 拦截器接口
 */
public interface AnnotationInterceptor<T extends Annotation> {

    /**
     * 获得当前拦截器对应注解类型
     */
    Class<T> getAnnotation();

    /**
     * 原始方法执行前调用
     */
    default void before(BeforeJoinPoint point) throws Throwable {

    }

    /**
     * 原始方法执行后调用
     */
    default void after(AfterJoinPoint point) throws Throwable {

    }

    /**
     * 原始方法抛出异常后调用
     */
    default void throwException(ThrowJoinPoint point) throws Throwable {

    }

}
