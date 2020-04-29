package lol.cicco.ioc.core.module.conditional;

import java.lang.annotation.Annotation;

public abstract class AbstractConditionalProcessor<T extends Annotation> implements ConditionalProcessor<T> {

    private final Class<T> annotationType;

    public AbstractConditionalProcessor(Class<T> type) {
        this.annotationType = type;
    }

    @Override
    public Class<T> getAnnotationType() {
        return annotationType;
    }

    @Override
    public boolean checkConditional(ConditionalBeanDefine beanDefine) {
        for(Annotation annotation : beanDefine.beanRegisterAnnotations()) {
            if(annotation.annotationType().equals(annotationType)) {
                return doChecker((T)annotation);
            }
        }
        return true;
    }

    public abstract boolean doChecker(T annotation);
}
