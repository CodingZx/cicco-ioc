package lol.cicco.ioc.core;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

@Data
public class BeanDefinition {

    BeanDefinition() {
    }

    /**
     * Bean可转换类型, 父类/接口类等
     */
    private Set<Class<?>> beanTypes;

    /**
     * 自身类型
     */
    private Class<?> selfType;

    /**
     * Bean名称
     */
    private String beanName;

}
