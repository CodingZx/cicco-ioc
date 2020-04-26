package lol.cicco.ioc.core.module.interceptor;

/**
 * Interceptor.throwException参数
 */
public interface ThrowJoinPoint extends BaseJoinPoint {

    /**
     * 当前方法抛出的异常信息
     */
    Throwable getThrowable();
}
