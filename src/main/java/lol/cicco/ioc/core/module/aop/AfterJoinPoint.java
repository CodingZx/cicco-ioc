package lol.cicco.ioc.core.module.aop;

public interface AfterJoinPoint extends BeforeJoinPoint {

    Object getReturnValue();

}
