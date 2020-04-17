package lol.cicco.ioc.core.module.aop;

import java.lang.reflect.Method;

public class JoinPointImpl implements AfterJoinPoint {
    private final Object target;
    private final Method method;
    private final Object[] args;
    private Object returnValue;

    public JoinPointImpl(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    @Override
    public Object getObject() {
        return target;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }
}
