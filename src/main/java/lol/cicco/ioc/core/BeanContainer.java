package lol.cicco.ioc.core;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.exception.BeanInitializeException;
import lol.cicco.ioc.core.exception.BeanNotFountException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class BeanContainer {

    private final Map<Class<?>, Object> typeBeans;

    private BeanContainer(){
        typeBeans = new LinkedHashMap<>();
    }

    static BeanContainer create(Set<BeanDefinition> definitions) {
        BeanContainer container = new BeanContainer();
        // 注册
        container.register(definitions);
        // 注入
        container.inject(definitions);
        return container;
    }


    private void register(Set<BeanDefinition> definitions) {
        for(BeanDefinition definition : definitions) {
            Class<?> type = definition.getBeanType();

            // 被重复扫描
            if(typeBeans.containsKey(type)) {
                log.debug("[{}] 已经被注册至IOC.. 跳过..", type.toString());
                continue;
            }

            try {
                Constructor<?> defConstructor = type.getConstructor();
                Object obj = defConstructor.newInstance();
                log.debug("Bean[{}]注册至Container...", definition.getBeanType().toString());
                typeBeans.put(definition.getBeanType(), obj);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new BeanInitializeException("["+type.toString()+"] 没有默认构造函数....", e);
            }
        }
    }

    private void inject(Set<BeanDefinition> definitions) {
        for(BeanDefinition definition : definitions) {
            Object bean = this.getBeanByType(definition.getBeanType());

            log.debug("执行依赖注入, 当前目标:{}", definition.getBeanType().toString());

            Field[] fields = definition.getBeanType().getDeclaredFields();
            for(Field field : fields) {
                if(field.getAnnotationsByType(Inject.class) == null) {
                    continue;
                }
                if(!field.canAccess(bean)) {
                    field.setAccessible(true);
                }

                Object injectObj = this.getBeanByType(field.getType());
                try {
                    field.set(bean, injectObj);
                } catch (IllegalAccessException ignore) {
                    // nop..
                }
            }
        }
    }


    public <T> T getBeanByType(Class<T> beanCls){
        T obj = (T)typeBeans.get(beanCls);

        if(obj == null) {
            throw new BeanNotFountException("["+beanCls.toString()+"] 未注册至IOC, 请检查["+ Registration.class +"]注解与初始化配置.");
        }
        return obj;
    }


}
