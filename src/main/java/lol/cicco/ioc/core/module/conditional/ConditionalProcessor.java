package lol.cicco.ioc.core.module.conditional;

import java.lang.annotation.Annotation;

public interface ConditionalProcessor<T extends Annotation> {

    /**
     * 注解类型
     */
    Class<T> getAnnotationType();

    /**
     * 校验BeanType是否有效
     */
    boolean checkConditional(ConditionalBeanDefine beanDefine);
}
