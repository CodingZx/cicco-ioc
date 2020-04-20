package lol.cicco.ioc.core.module.aop;

import java.lang.reflect.Method;

/**
 * Interceptor.before参数
 */
public interface BeforeJoinPoint extends BaseJoinPoint {

    /**
     * 当前执行方法的实例
     */
    Object getObject();

    /**
     * 当前方法参数
     */
    Object[] getArgs();

    /**
     * 当前执行方法
     */
    Method getMethod();
}
