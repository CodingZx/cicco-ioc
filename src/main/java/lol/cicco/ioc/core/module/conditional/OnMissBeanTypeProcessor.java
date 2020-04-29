package lol.cicco.ioc.core.module.conditional;

import lol.cicco.ioc.annotation.ConditionalOnMissBeanType;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;

import java.util.Set;

public class OnMissBeanTypeProcessor extends AbstractConditionalProcessor<ConditionalOnMissBeanType> {

    private final BeanRegistry beanRegistry;

    public OnMissBeanTypeProcessor(BeanRegistry beanRegistry) {
        super(ConditionalOnMissBeanType.class);
        this.beanRegistry = beanRegistry;
    }

    @Override
    public boolean doChecker(ConditionalOnMissBeanType annotation) {
        Class<?>[] dependBeanTypes = annotation.value();
        for(Class<?> depend : dependBeanTypes) {
            Set<BeanProvider> beanProviders = beanRegistry.getNullableBeans(depend);
            // 当某一个指定的Type已经存在... 则直接return false..
            if(beanProviders != null) {
                return false;
            }
        }
        return true;
    }
}
