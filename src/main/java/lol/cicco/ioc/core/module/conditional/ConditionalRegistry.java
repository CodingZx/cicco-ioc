package lol.cicco.ioc.core.module.conditional;

import java.lang.annotation.Annotation;

public interface ConditionalRegistry {

    /**
     * 注册Conditional注解处理器
     */
    void register(ConditionalProcessor<? extends Annotation> processor);

    /**
     * 是否包含需要校验的Conditional注解
     */
    boolean hasConditionalAnnotation(ConditionalBeanDefine beanDefine);

    /**
     * 校验BeanType是否有效
     */
    boolean checkConditional(ConditionalBeanDefine beanDefine);
}
