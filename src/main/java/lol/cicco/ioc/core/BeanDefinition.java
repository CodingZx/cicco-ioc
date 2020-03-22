package lol.cicco.ioc.core;

import lombok.Data;

@Data
public class BeanDefinition {

    BeanDefinition(){}

    /**
     * Bean自身类型
     */
    private Class<?> beanType;

}
