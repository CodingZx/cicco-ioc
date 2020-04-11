package lol.cicco.ioc.core.aop;

import java.lang.reflect.Method;

public interface BeanProxy {

    void setProcessor(AopProcessor processor);

    void putMethod(String name, Method method);

    Method getMethod(String name);

}
