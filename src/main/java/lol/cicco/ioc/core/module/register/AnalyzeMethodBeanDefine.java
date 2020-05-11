package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.annotation.Registration;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Data
@EqualsAndHashCode(callSuper = true)
class AnalyzeMethodBeanDefine extends AnalyzeBeanDefine {

    private String invokeBeanName;

    private Method defineMethod;

    public AnalyzeMethodBeanDefine(Class<?> beanType, Registration registration, Method defineMethod, String invokeBeanName, Annotation[] registerBeanAnnotations) {
        super(beanType, registration, defineMethod, registerBeanAnnotations);
        this.defineMethod = defineMethod;
        this.invokeBeanName = invokeBeanName;
    }

}
