package lol.cicco.ioc.core.module.aop;

/**
 * Interceptor.after参数
 */
public interface AfterJoinPoint extends BaseJoinPoint {

    /**
     * 当前方法执行后返回值
     */
    Object getReturnValue();

}
