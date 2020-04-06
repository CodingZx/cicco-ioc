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
    private final Map<Class<?>, String> typeBeans;
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

    private static void setBindHandler(List<BindHandler<?>> bindHandlers) {
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
            Class<?> type = definition.getBeanType();

            // 被重复注册
            if (nameBeans.containsKey(definition.getBeanName())) {
                throw new BeanDefinitionStoreException("BeanName["+definition.getBeanName()+"], Class["+definition.getBeanType().getTypeName()+"] 已经被注册至IOC..");
            }

            try {
                Constructor<?> defConstructor = type.getConstructor();
                Object obj = defConstructor.newInstance();
                log.debug("Bean[{}]注册至Container...", definition.getBeanType().toString());

                nameBeans.put(definition.getBeanName(), obj);
                typeBeans.put(definition.getBeanType(), definition.getBeanName());
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new BeanInitializeException("[" + type.toString() + "] 没有默认构造函数....", e);
            }
        }
    }

    @SneakyThrows
    private void inject(Set<BeanDefinition> definitions) {
        for (BeanDefinition definition : definitions) {
            Object bean = this.getBeanByType(definition.getBeanType());

            log.debug("执行依赖注入, 当前目标:{}", definition.getBeanType().toString());

            Field[] fields = definition.getBeanType().getDeclaredFields();
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

                    Object injectObj = null;
                    if("".equals(inject.byName().trim())) {
                        injectObj = getBeanByName(inject.byName().trim());
                    } else {
                        injectObj = getNullableBean(field.getType());
                    }

                    if(inject.required()) {
                        if (injectObj == null) {
                            throw new BeanNotFountException("[" + field.getType().getTypeName() + "] 未注册至IOC, 请检查[" + Registration.class + "]注解与初始化配置.");
                        }
                    }

                    if(injectObj != null) {
                        field.set(bean, getBeanByType(field.getType()));
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
        String beanName = typeBeans.get(beanCls);
        if(beanName == null) {
            return null;
        }
        return (T)nameBeans.get(beanName);
    }

    public <T> T getBeanByName(String beanName) {
        T obj = (T) nameBeans.get(beanName);
        if (obj == null) {
            throw new BeanNotFountException("[" + beanName + "] 未注册至IOC, 请检查[" + Registration.class + "]注解与初始化配置.");
        }
        return obj;
    }

    public String getProperty(String key, String defaultValue) {
        return propValues.getOrDefault(key, defaultValue);
    }

}
