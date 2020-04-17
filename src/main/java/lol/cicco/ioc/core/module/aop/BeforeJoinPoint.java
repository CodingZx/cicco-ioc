package lol.cicco.ioc.core.module.aop;

import java.lang.reflect.Method;

public interface BeforeJoinPoint {

    Object getObject();

    Object[] getArgs();

    Method getMethod();
}
