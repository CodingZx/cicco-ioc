package lol.cicco.ioc.core.aop;

public interface AfterJoinPoint extends BeforeJoinPoint {

    Object getReturnValue();

}
