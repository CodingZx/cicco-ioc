package lol.cicco.ioc.core.module.conditional;

import java.lang.annotation.Annotation;

public interface ConditionalBeanDefine {

    /**
     * Bean定义注解
     */
    Annotation[] beanRegisterAnnotations();

}
