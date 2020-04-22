package lol.cicco.ioc.core.module.register;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Set;

@Data
class AnalyzeBeanDefine {
    // bean类型
    private Class<?> beanType;
    // Bean名称
    private String beanName;
    // 参数类型
    private Class<?>[] parameterTypes;
    // 参数注解
    private Annotation[][] parameterAnnotations;
    // 实现的类接口等
    private Set<Class<?>> castClasses;
}
