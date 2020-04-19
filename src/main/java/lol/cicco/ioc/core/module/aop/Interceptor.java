package lol.cicco.ioc.core.module.aop;

import java.lang.annotation.Annotation;

public interface Interceptor<T extends Annotation> {

    Class<T> getAnnotation();

    default void before(BeforeJoinPoint point) throws Throwable {

    }

    default void after(AfterJoinPoint point) throws Throwable {

    }

    default void throwException(ThrowJoinPoint point) throws Throwable {

    }

}
