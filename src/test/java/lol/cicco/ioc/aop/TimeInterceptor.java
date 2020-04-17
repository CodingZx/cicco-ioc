package lol.cicco.ioc.aop;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.module.aop.AfterJoinPoint;
import lol.cicco.ioc.core.module.aop.BeforeJoinPoint;
import lol.cicco.ioc.core.module.aop.Interceptor;

@Registration
public class TimeInterceptor implements Interceptor<SystemClock> {

    private final ThreadLocal<Long> threadLocal;

    public TimeInterceptor() {
        this.threadLocal = new ThreadLocal<>();
    }

    @Override
    public Class<SystemClock> getAnnotation() {
        return SystemClock.class;
    }

    @Override
    public void before(BeforeJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        System.out.println("执行开始时间: " + start);
        threadLocal.set(start);
    }

    @Override
    public void after(AfterJoinPoint point) throws Throwable {
        long start = threadLocal.get();
        long end = System.currentTimeMillis();
        System.out.println("执行结束时间: " + end);
        System.out.println("总执行时间  : " + (end - start) + "ms");
        threadLocal.remove();
    }
}
