package lol.cicco.ioc.core;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.aop.AopProcessor;
import lol.cicco.ioc.core.aop.Interceptor;
import lol.cicco.ioc.core.binder.PropertyHandler;
import lol.cicco.ioc.core.binder.PropertyProcessor;
import lol.cicco.ioc.core.exception.BeanDefinitionStoreException;
import lol.cicco.ioc.core.exception.BeanInitializeException;
import lol.cicco.ioc.core.exception.BeanNotFountException;
import lol.cicco.ioc.core.exception.PropertyConvertException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
class CiccoContainer {
    private final PropertyProcessor propertyProcessor;
    private final AopProcessor aopProcessor;
    // Property
    private final Map<String, String> propValues;

    // Bean
    private final Map<Class<?>, List<String>> typeBeans;
    private final Map<String, Object> nameBeans;

    private CiccoContainer() {
        typeBeans = new LinkedHashMap<>();
        propValues = new LinkedHashMap<>();
        nameBeans = new LinkedHashMap<>();
        aopProcessor = new AopProcessor();
        propertyProcessor = PropertyProcessor.getInstance();
    }

    /**
     * 根据初始化信息创建Container
     */
    static CiccoContainer create(Initialize initialize) {
        CiccoContainer container = new CiccoContainer();
        // 注册Aop拦截器
        container.registerAopInterceptor(initialize.getInterceptors());
        // 注册转换器
        container.registerPropertyHandler(initialize.getPropertyHandlers());
        // 加载属性信息
        container.loadProperties(initialize.getLoadPropertyFiles());
        // 扫描注册Bean
        Set<BeanDefinition> beanDefinitions = container.doScanBeans(initialize.getScanPackages());
        // 注册
        container.registerBeans(beanDefinitions);
        // 注入
        container.inject(beanDefinitions);
        return container;
    }

    /**
     * 注册Aop拦截器
     */
    protected void registerAopInterceptor(Map<Class<? extends Annotation>, Interceptor> interceptors) {
        aopProcessor.register(interceptors);
    }

    /**
     * 注册属性转换器
     */
    private void registerPropertyHandler(Collection<PropertyHandler<?>> handlers) {
        propertyProcessor.registerHandler(handlers);
    }

    /**
     * 执行扫描器, 扫描注册Bean
     */
    private Set<BeanDefinition> doScanBeans(Set<String> scanPackages) {
        ClassPathScanner scanner = new ClassPathScanner();
        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();
        for (String pkg : scanPackages) {
            beanDefinitions.addAll(scanner.doScan(pkg, Initialize.class.getClassLoader()));
        }
        return beanDefinitions;
    }

    /**
     * 加载对应配置文件
     */
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

    /**
     * 对应Bean信息注册至IOC
     */
    private void registerBeans(Set<BeanDefinition> definitions) {
        for (BeanDefinition definition : definitions) {
            Class<?> type = definition.getSelfType();

            // 被重复注册
            if (nameBeans.containsKey(definition.getBeanName())) {
                throw new BeanDefinitionStoreException("BeanName[" + definition.getBeanName() + "], Class[" + definition.getSelfType().getTypeName() + "] 已经被注册至IOC..");
            }
            try {
                type.getConstructor(); // 校验是否存在默认构造函数
            }catch (NoSuchMethodException e) {
                throw new BeanInitializeException("[" + type.toString() + "] 需要使用默认构造函数....", e);
            }

            Object obj = aopProcessor.beanEnhance(definition);
            nameBeans.put(definition.getBeanName(), obj);

            log.debug("Bean[{}]注册至Container...", definition.getSelfType().toString());

            for (Class<?> castType : definition.getBeanTypes()) {
                List<String> beanNames = typeBeans.getOrDefault(castType, new LinkedList<>());
                beanNames.add(definition.getBeanName());
                typeBeans.put(castType, beanNames);
            }
        }
    }

    /**
     * 执行注入
     */
    @SneakyThrows
    private void inject(Set<BeanDefinition> definitions) {
        for (BeanDefinition definition : definitions) {
            Object bean = getBeanByType(definition.getSelfType());

            log.debug("执行依赖注入, 当前目标:{}", definition.getSelfType().toString());

            Field[] fields = definition.getSelfType().getDeclaredFields();
            for (Field field : fields) {
                Annotation injectAnnotation = getInjectAnnotation(definition, field);
                if (injectAnnotation == null) {
                    continue;
                }
                if (!field.canAccess(bean)) {
                    field.setAccessible(true);
                }

                if (injectAnnotation.annotationType() == Inject.class) {
                    Inject inject = (Inject) injectAnnotation;

                    Object injectObj;
                    if (!"".equals(inject.byName().trim())) {
                        injectObj = getNullableBean(inject.byName().trim());
                    } else {
                        injectObj = getNullableBean(field.getType());
                    }

                    if (inject.required()) {
                        if (injectObj == null) {
                            throw new BeanNotFountException("[" + field.getType().getTypeName() + "] 未注册至IOC, 请检查[" + Registration.class + "]注解与初始化配置.");
                        }
                    }

                    if (injectObj != null) {
                        field.set(bean, injectObj);
                    }
                }
                if (injectAnnotation.annotationType() == Binder.class) {
                    Binder binder = (Binder) injectAnnotation;
                    String boundDefVal = binder.defaultValue();
                    if (boundDefVal.equals("")) {
                        boundDefVal = null;
                    }
                    String propertyValue = propValues.getOrDefault(binder.value().trim(), boundDefVal);
                    if (propertyValue == null || propertyValue.trim().equals("")) {
                        throw new PropertyConvertException("Property [" + binder.value() + "] 未配置, 请检查对应配置文件...");
                    }
                    // 执行属性注入
                    field.set(bean, propertyProcessor.covertValue(binder.value(), propertyValue, field.getType()));
                }
            }
        }
    }

    /**
     * 获取属性注入注解
     */
    private Annotation getInjectAnnotation(BeanDefinition definition, Field field) {
        var inject = field.getAnnotation(Inject.class);
        var binder = field.getAnnotation(Binder.class);

        if (inject != null && binder != null) {
            throw new BeanDefinitionStoreException("Bean[" + definition.getBeanName() + "]的[" + field.getName() + "]属性无法同时使用@Inject和@Binder.. 请修改对应代码..");
        }

        if (inject != null) {
            return inject;
        }
        return binder;
    }

    /**
     * 根据类型获得IOC中已注册的Bean实例 <br>
     * 若Bean不存在, 则抛出BeanNotFountException异常
     */
    public <T> T getBeanByType(Class<T> beanCls) {
        T obj = getNullableBean(beanCls);
        if (obj == null) {
            throw new BeanNotFountException("[" + beanCls.toString() + "] 未注册至IOC, 请检查[" + Registration.class + "]注解与初始化配置.");
        }
        return obj;
    }

    /**
     * 根据类型获得IOC中已注册的Bean实例 <br>
     * 未注册的类型返回Null <br>
     * 已注册的类型若存在多个对应实例, 则抛出BeanDefinitionStoreException异常
     */
    <T> T getNullableBean(Class<?> beanCls) {
        List<String> beanNames = typeBeans.get(beanCls);
        if (beanNames == null) {
            return null;
        }
        if (beanNames.size() == 1) {
            return (T) nameBeans.get(beanNames.get(0));
        }

        StringJoiner joiner = new StringJoiner(",");
        beanNames.forEach(joiner::add);
        throw new BeanDefinitionStoreException("存在多个对应Bean[" + beanCls + "], 请指定BeanName... 当前IOC中已存在对应BeanName为:[" + joiner.toString() + "]");
    }

    /**
     * 根据BeanName获得IOC中已注册的Bean实例 <br>
     * 若Bean不存在, 则抛出BeanNotFountException异常
     */
    public <T> T getBeanByName(String beanName) {
        T obj = getNullableBean(beanName);
        if (obj == null) {
            throw new BeanNotFountException("[" + beanName + "] 未注册至IOC, 请检查[" + Registration.class + "]注解与初始化配置.");
        }
        return obj;
    }

    /**
     * 根据BeanName获得IOC中已注册的Bean实例 <br>
     * 未注册的BeanName返回Null
     */
    <T> T getNullableBean(String beanName) {
        return (T) nameBeans.get(beanName);
    }

    /**
     * 根据提供属性名称获得对应属性值
     */
    public String getProperty(String propertyName, String defaultValue) {
        return propValues.getOrDefault(propertyName, defaultValue);
    }

    /**
     * 根据提供的属性名 将属性值转换为对应类型
     */
    public <T> T getProperty(String propertyName, Class<T> cls) {
        String propertyVal = getProperty(propertyName, (String) null);
        return propertyProcessor.covertValue(propertyName, propertyVal, cls);
    }

}
