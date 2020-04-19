package lol.cicco.ioc.core.module.aop;

public interface ThrowJoinPoint extends BeforeJoinPoint{

    Throwable getThrowable();
}
