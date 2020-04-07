package lol.cicco.ioc.core;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.binder.BindHandler;
import lol.cicco.ioc.core.binder.BinderProcessor;
import lol.cicco.ioc.core.exception.BeanDefinitionStoreException;
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
class CiccoContainer {
    // Property
    private final Map<String, String> propValues;

    // Bean
    private final Map<Class<?>, List<String>> typeBeans;
    private final Map<String, Object> nameBeans;

    private CiccoContainer() {
        typeBeans = new LinkedHashMap<>();
        propValues = new LinkedHashMap<>();
        nameBeans = new LinkedHashMap<>();
    }

    static CiccoContainer create(Initialize initialize) {
        setBindHandler(initialize.getBindHandlers());

        CiccoContainer container = new CiccoContainer();

        // 加载属性信息
        container.loadProperties(initialize.getLoadProperties());

        // 扫描注册Bean
        Set<BeanDefinition> beanDefinitions = container.doScanBeans(initialize.getScanPackages());
        // 注册
        container.register(beanDefinitions);
        // 注入
        container.inject(beanDefinitions);
        return container;
    }

    private static void setBindHandler(Collection<BindHandler<?>> bindHandlers) {
        // 注册BinderHandler
        BinderProcessor binder = BinderProcessor.getInstance();
        for (BindHandler<?> handler : bindHandlers) {
            binder.registerHandler(handler);
        }
    }

    private Set<BeanDefinition> doScanBeans(Set<String> scanPackages) {
        ClassPathScanner scanner = new ClassPathScanner();
        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();
        for (String pkg : scanPackages) {
            beanDefinitions.addAll(scanner.doScan(pkg, Initialize.class.getClassLoader()));
        }
        return beanDefinitions;
    }

    @SneakyThrows
    private void loadProperties(Set<String> loadProperties) {
        // 加载对应配置
        for (String propertyName : loadProperties) {
            var inputStream = CiccoContainer.class.getResourceAsStream(propertyName);
            if (inputStream == null) {
                log.warn("[{}] 未找到对应文件....", propertyName);
                continue;
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            for (var key : properties.keySet()) {
                var value = properties.getProperty(key.toString());
                propValues.put(key.toString(), value);
            }
        }
    }

    private void register(Set<BeanDefinition> definitions) {
        for (BeanDefinition definition : definitions) {
            Class<?> type = definition.getSelfType();

            // 被重复注册
            if (nameBeans.containsKey(definition.getBeanName())) {
                throw new BeanDefinitionStoreException("BeanName["+definition.getBeanName()+"], Class["+definition.getSelfType().getTypeName()+"] 已经被注册至IOC..");
            }

            try {
                Constructor<?> defConstructor = type.getConstructor();
                Object obj = defConstructor.newInstance();
                log.debug("Bean[{}]注册至Container...", definition.getSelfType().toString());

                nameBeans.put(definition.getBeanName(), obj);

                for(Class<?> castType : definition.getBeanTypes()) {
                    List<String> beanNames = typeBeans.getOrDefault(castType, new LinkedList<>());
                    beanNames.add(definition.getBeanName());
                    typeBeans.put(castType, beanNames);
                }

            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new BeanInitializeException("[" + type.toString() + "] 需要使用默认构造函数....", e);
            }
        }
    }

    @SneakyThrows
    private void inject(Set<BeanDefinition> definitions) {
        for (BeanDefinition definition : definitions) {
            Object bean = this.getBeanByType(definition.getSelfType());

            log.debug("执行依赖注入, 当前目标:{}", definition.getSelfType().toString());

            Field[] fields = definition.getSelfType().getDeclaredFields();
            for (Field field : fields) {
                Annotation injectAnnotation = canInject(field);
                if (injectAnnotation == null) {
                    continue;
                }
                if (!field.canAccess(bean)) {
                    field.setAccessible(true);
                }

                if (injectAnnotation.annotationType() == Inject.class) {
                    Inject inject = (Inject) injectAnnotation;

                    Object injectObj;
                    if(!"".equals(inject.byName().trim())) {
                        injectObj = getNullableBean(inject.byName().trim());
                    } else {
                        injectObj = getNullableBean(field.getType());
                    }

                    if(inject.required()) {
                        if (injectObj == null) {
                            throw new BeanNotFountException("[" + field.getType().getTypeName() + "] 未注册至IOC, 请检查[" + Registration.class + "]注解与初始化配置.");
                        }
                    }

                    if(injectObj != null) {
                        field.set(bean, injectObj);
                    }
                }
                if (injectAnnotation.annotationType() == Binder.class) {
                    Binder binder = (Binder) injectAnnotation;
                    String propertyValue = propValues.get(binder.value());
                    if (propertyValue == null) {
                        throw new PropertyBindException("Property [" + binder.value() + "] 未配置, 请检查对应配置文件...");
                    }
                    // 执行属性注入
                    field.set(bean, BinderProcessor.getInstance().covertValue(binder.value(), propertyValue, field.getGenericType()));
                }
            }
        }
    }

    private Annotation canInject(Field field) {
        var inject = field.getAnnotation(Inject.class);
        if (inject != null) {
            return inject;
        }
        var binder = field.getAnnotation(Binder.class);
        if (binder != null) {
            return binder;
        }
        return null;
    }

    public <T> T getBeanByType(Class<T> beanCls) {
        T obj = getNullableBean(beanCls);
        if (obj == null) {
            throw new BeanNotFountException("[" + beanCls.toString() + "] 未注册至IOC, 请检查[" + Registration.class + "]注解与初始化配置.");
        }
        return obj;
    }

    <T> T getNullableBean(Class<?> beanCls) {
        List<String> beanNames = typeBeans.get(beanCls);
        if(beanNames == null) {
            return null;
        }
        if(beanNames.size() == 1) {
            return (T)nameBeans.get(beanNames.get(0));
        }

        StringJoiner joiner = new StringJoiner(",");
        beanNames.forEach(joiner::add);
        throw new BeanDefinitionStoreException("存在多个对应Bean["+beanCls+"], 请指定注入BeanName...已存在BeanName为:{"+joiner.toString()+"}");
    }

    public <T> T getBeanByName(String beanName) {
        T obj = getNullableBean(beanName);
        if (obj == null) {
            throw new BeanNotFountException("[" + beanName + "] 未注册至IOC, 请检查[" + Registration.class + "]注解与初始化配置.");
        }
        return obj;
    }

    <T> T getNullableBean(String beanName) {
        return (T) nameBeans.get(beanName);
    }

    public String getProperty(String key, String defaultValue) {
        return propValues.getOrDefault(key, defaultValue);
    }

}
