package lol.cicco.ioc.core;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.exception.BeanNotFountException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BeanContainer {

    private static final Map<Class<?>, Object> typeBeans = new HashMap<>();

    void registerBean(BeanDefinition definition, Object object) {
        log.debug("Bean[{}]注册至Container...", definition.getBeanType().toString());
        typeBeans.put(definition.getBeanType(), object);
    }

    public boolean contains(Class<?> beanCls) {
        return typeBeans.containsKey(beanCls);
    }

    public <T> T getBeanByType(Class<T> beanCls){
        T obj = (T)typeBeans.get(beanCls);

        if(obj == null) {
            throw new BeanNotFountException("["+beanCls.toString()+"] 未注册至IOC, 请检查["+ Registration.class +"]注解与初始化配置.");
        }
        return obj;
    }
}
