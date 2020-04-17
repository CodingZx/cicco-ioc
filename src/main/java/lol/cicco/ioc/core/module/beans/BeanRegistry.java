package lol.cicco.ioc.core.module.beans;

import java.util.Set;

public interface BeanRegistry {

    /**
     * 注册Bean
     */
    void register(Class<?> beanType, String beanName, BeanProvider provider);

    /**
     * 注册bean
     */
    void register(Class<?> beanType, String beanName, BeanProvider provider, boolean override);

    /**
     * 获得所有注册Bean的名称
     */
    Set<String> getRegisterBeans();

    /**
     * 根据BeanName获得BeanProvider
     */
    BeanProvider getNullableBean(String beanName);

    /**
     * 根据BeanType获得BeanProvider
     */
    BeanProvider getNullableBean(Class<?> beanType) throws IllegalStateException;

    /**
     * 根据BeanType获得对应BeanProvider
     */
    Set<BeanProvider> getNullableBeans(Class<?> beanType);
}
