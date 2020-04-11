package lol.cicco.ioc.aop;

import lol.cicco.ioc.core.aop.AfterJoinPoint;
import lol.cicco.ioc.core.aop.BeforeJoinPoint;
import lol.cicco.ioc.core.aop.Interceptor;

public class TimeInterceptor implements Interceptor {

    private long start;

    @Override
    public void before(BeforeJoinPoint point) throws Throwable {
        start = System.currentTimeMillis();
        System.out.println("执行开始时间: " + start);
    }

    @Override
    public void after(AfterJoinPoint point) throws Throwable {
        long end = System.currentTimeMillis();
        System.out.println("执行结束时间: " + end);
        System.out.println("总执行时间  : " + (end - start) + "ms");
    }
}
