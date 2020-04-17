package lol.cicco.ioc.aop;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.module.aop.AfterJoinPoint;
import lol.cicco.ioc.core.module.aop.BeforeJoinPoint;
import lol.cicco.ioc.core.module.aop.Interceptor;

@Registration
public class LogInterceptor implements Interceptor<SystemLog> {
    @Override
    public Class<SystemLog> getAnnotation() {
        return SystemLog.class;
    }

    @Override
    public void before(BeforeJoinPoint point) throws Throwable {
        System.out.println(point.getMethod() + "开始执行");
    }

    @Override
    public void after(AfterJoinPoint point) throws Throwable {
        System.out.println(point.getMethod() + "结束执行");

    }
}
