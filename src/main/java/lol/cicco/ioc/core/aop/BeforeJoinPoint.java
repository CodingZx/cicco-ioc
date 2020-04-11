package lol.cicco.ioc.core.aop;

import java.lang.reflect.Method;

public interface BeforeJoinPoint {

    Object getThis();

    Object[] getArgs();

    Method getMethod();
}
