package lol.cicco.ioc.core.aop;

public interface Interceptor {

    default void before(BeforeJoinPoint point) throws Throwable {

    }

    default void after(AfterJoinPoint point) throws Throwable {

    }
}
