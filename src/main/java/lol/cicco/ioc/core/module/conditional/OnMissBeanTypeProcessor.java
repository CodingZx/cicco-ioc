package lol.cicco.ioc.core.module.conditional;

import lol.cicco.ioc.annotation.ConditionalOnMissBeanType;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;

import java.lang.annotation.Annotation;
import java.util.Set;

public class OnMissBeanTypeProcessor implements ConditionalProcessor<ConditionalOnMissBeanType> {

    private final BeanRegistry beanRegistry;

    public OnMissBeanTypeProcessor(BeanRegistry beanRegistry) {
        this.beanRegistry = beanRegistry;
    }

    @Override
    public Class<ConditionalOnMissBeanType> getAnnotationType() {
        return ConditionalOnMissBeanType.class;
    }

    @Override
    public boolean checkConditional(ConditionalBeanDefine beanDefine) {
        for(Annotation annotation : beanDefine.beanRegisterAnnotations()) {
            if(annotation.annotationType().equals(ConditionalOnMissBeanType.class)) {
                Class<?>[] dependBeanTypes = ((ConditionalOnMissBeanType)annotation).value();
                for(Class<?> depend : dependBeanTypes) {
                    Set<BeanProvider> beanProviders = beanRegistry.getNullableBeans(depend);
                    // 当某一个指定的Type已经存在... 则直接return false..
                    if(beanProviders != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
