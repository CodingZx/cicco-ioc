package lol.cicco.ioc.aop;

import lol.cicco.ioc.core.aop.AfterJoinPoint;
import lol.cicco.ioc.core.aop.BeforeJoinPoint;
import lol.cicco.ioc.core.aop.Interceptor;

public class LogInterceptor implements Interceptor {
    @Override
    public void before(BeforeJoinPoint point) throws Throwable {
        System.out.println(point.getMethod() + "开始执行");
    }

    @Override
    public void after(AfterJoinPoint point) throws Throwable {
        System.out.println(point.getMethod() + "结束执行");

    }
}
