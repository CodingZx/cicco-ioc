package lol.cicco.ioc.core.module.beans;

import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class BeanModule implements CiccoModule<BeanRegistry>, BeanRegistry {
    public static final String BEAN_MODULE_NAME = "_beanModule";

    private final Map<String, BeanProvider> nameBeans = new LinkedHashMap<>();
    private final Map<Class<?>, Set<String>> typeBeans = new LinkedHashMap<>();

    @Override
    public void initModule(CiccoContext context) {
        log.debug("init bean module.....");
    }

    @Override
    public String getModuleName() {
        return BEAN_MODULE_NAME;
    }

    @Override
    public BeanRegistry getModuleProcessor() {
        return this;
    }

    @Override
    public List<String> dependOn() {
        return null;
    }

    @Override
    public void register(Class<?> beanType, String beanName, BeanProvider provider) {
        register(beanType, beanName, provider, false);
    }

    @Override
    public void register(Class<?> beanType, String beanName, BeanProvider provider, boolean override) {
        synchronized (nameBeans) {
            if (!override) {
                Object oldBean = nameBeans.get(beanName);
                if (oldBean != null) {
                    throw new IllegalStateException("BeanName[" + beanName + "] 已经被注册至IOC..");
                }
            }
            nameBeans.put(beanName, provider);

            for(Class<?> type : getTypes(beanType)) {
                Set<String> beanNames = typeBeans.getOrDefault(type, new LinkedHashSet<>());
                beanNames.add(beanName);
                typeBeans.put(type, beanNames);
            }
        }
    }

    @Override
    public Set<String> getRegisterBeans() {
        return nameBeans.keySet();
    }

    @Override
    public BeanProvider getNullableBean(String beanName) {
        return nameBeans.get(beanName);
    }

    @Override
    public BeanProvider getNullableBean(Class<?> beanType) throws IllegalStateException {
        Set<String> beanNames = typeBeans.get(beanType);
        if (beanNames == null) {
            return null;
        }
        if (beanNames.size() == 1) {
            return nameBeans.get(beanNames.iterator().next());
        }

        StringJoiner joiner = new StringJoiner(",");
        beanNames.forEach(joiner::add);
        throw new IllegalStateException("存在多个对应Bean[" + beanType + "], 请指定BeanName... 当前IOC中已存在对应BeanName为:[" + joiner.toString() + "]");
    }

    @Override
    public Set<BeanProvider> getNullableBeans(Class<?> beanType) {
        Set<String> beanNames = typeBeans.get(beanType);
        if (beanNames == null) {
            return null;
        }
        return beanNames.stream().map(nameBeans::get).collect(Collectors.toSet());
    }


    private Set<Class<?>> getTypes(Class<?> cls) {
        if (Object.class.equals(cls)) {
            return new HashSet<>();
        }
        Set<Class<?>> allCastClasses = new HashSet<>();
        allCastClasses.add(cls); // 添加自身

        for (Class<?> clsInterface : cls.getInterfaces()) {
            allCastClasses.addAll(getTypes(clsInterface));
        }

        Class<?> superCls = cls.getSuperclass();
        if (superCls != null) {
            allCastClasses.addAll(getTypes(superCls));
        }
        return allCastClasses;
    }
}
