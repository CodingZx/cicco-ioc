package lol.cicco.ioc.aop;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.module.interceptor.AfterJoinPoint;
import lol.cicco.ioc.core.module.interceptor.AnnotationInterceptor;
import lol.cicco.ioc.core.module.interceptor.BeforeJoinPoint;

@Registration
public class LogAnnotationInterceptor implements AnnotationInterceptor<SystemLog> {
    @Override
    public Class<SystemLog> getAnnotation() {
        return SystemLog.class;
    }

    @Override
    public void before(BeforeJoinPoint point) {
        System.out.println(point.getMethod() + "开始执行");
    }

    @Override
    public void after(AfterJoinPoint point) {
        System.out.println(point.getMethod() + "结束执行");

    }
}
