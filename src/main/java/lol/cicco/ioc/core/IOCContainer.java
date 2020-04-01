package lol.cicco.ioc.core;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.binder.BinderProcessor;
import lol.cicco.ioc.core.exception.BeanInitializeException;
import lol.cicco.ioc.core.exception.BeanNotFountException;
import lol.cicco.ioc.core.exception.PropertyBindException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Slf4j
class IOCContainer {
    // Property
    private final Map<String,String> propValues;

    // Bean
    private final Map<Class<?>, Object> typeBeans;

    private IOCContainer(){
        typeBeans = new LinkedHashMap<>();
        propValues = new LinkedHashMap<>();
    }

    static IOCContainer create(Initialize initialize) {
        IOCContainer container = new IOCContainer();

        Set<BeanDefinition> beanDefinitions = container.doScan(initialize);
        // 注册
        container.register(beanDefinitions);
        // 注入
        container.inject(beanDefinitions);
        return container;
    }

    @SneakyThrows
    private Set<BeanDefinition> doScan(Initialize initialize) {
        ClassPathScanner scanner = new ClassPathScanner();
        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();
        for(String pkg : initialize.getScanPackages()) {
            beanDefinitions.addAll(scanner.doScan(pkg, Initialize.class.getClassLoader()));
        }
        // 加载对应配置
        for(String propertyName : initialize.getLoadProperties()) {
            var inputStream = IOCContainer.class.getResourceAsStream(propertyName);
            if(inputStream == null) {
                log.warn("[{}] 未找到对应文件....", propertyName);
            } else {
                Properties properties = new Properties();
                properties.load(inputStream);
                for(var key : properties.keySet()) {
                    var value = properties.getProperty(key.toString());
                    propValues.put(key.toString(), value);
                }
            }
        }
        return beanDefinitions;
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

    @SneakyThrows
    private void inject(Set<BeanDefinition> definitions) {
        for(BeanDefinition definition : definitions) {
            Object bean = this.getBeanByType(definition.getBeanType());

            log.debug("执行依赖注入, 当前目标:{}", definition.getBeanType().toString());

            Field[] fields = definition.getBeanType().getDeclaredFields();
            for(Field field : fields) {
                Annotation injectAnnotation = canInject(field);
                if(injectAnnotation == null) {
                    continue;
                }
                if(!field.canAccess(bean)) {
                    field.setAccessible(true);
                }

                if(injectAnnotation.annotationType() == Inject.class) {
                    Object injectObj = this.getBeanByType(field.getType());
                    field.set(bean, injectObj);
                }
                if(injectAnnotation.annotationType() == Binder.class) {
                    Binder binder = (Binder)injectAnnotation;
                    String propertyValue = propValues.get(binder.value());
                    if(propertyValue == null){
                        throw new PropertyBindException("Property ["+binder.value()+"] 未配置, 请检查对应配置文件...");
                    }

                    System.out.println(field.getType());

                    // 注入
                    field.set(bean, BinderProcessor.getInstance().covertValue(binder.value(), propertyValue, field.getGenericType()));
                }
            }
        }
    }

    private Annotation canInject(Field field) {
        var inject = field.getAnnotation(Inject.class);
        if(inject != null) {
            return inject;
        }
        var binder = field.getAnnotation(Binder.class);
        if(binder != null) {
            return binder;
        }
        return null;
    }

    public <T> T getBeanByType(Class<T> beanCls){
        T obj = (T)typeBeans.get(beanCls);

        if(obj == null) {
            throw new BeanNotFountException("["+beanCls.toString()+"] 未注册至IOC, 请检查["+ Registration.class +"]注解与初始化配置.");
        }
        return obj;
    }

    public String getProperty(String key, String defaultValue) {
        return propValues.getOrDefault(key, defaultValue);
    }

}
