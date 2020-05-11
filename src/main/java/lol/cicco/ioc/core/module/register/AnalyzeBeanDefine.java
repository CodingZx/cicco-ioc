package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.util.ClassUtils;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
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

    // Bean定义时的注解信息
    private Annotation[] registerAnnotations;

    public AnalyzeBeanDefine(Class<?> beanType, Registration registration, Executable executable, Annotation[] registerAnnotations) {
        this.beanType = beanType;
        this.beanName = "".equals(registration.name().trim()) ? beanType.getName() : registration.name().trim();
        this.parameterTypes = executable.getParameterTypes();
        this.parameterAnnotations = executable.getParameterAnnotations();
        this.castClasses = ClassUtils.getClassTypes(beanType);
        this.registerAnnotations = registerAnnotations;
    }
}
