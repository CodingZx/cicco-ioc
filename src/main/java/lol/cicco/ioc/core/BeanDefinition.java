package lol.cicco.ioc.core;

import lombok.Data;

import java.util.Set;

@Data
public class BeanDefinition {

    BeanDefinition(){}

    /**
     * Bean可转换类型
     */
    private Set<Class<?>> beanTypes;

    private Class<?> selfType;

    /**
     * Bean名称
     */
    private String beanName;

}
