package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.annotation.Registration;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Method;

@Data
@EqualsAndHashCode(callSuper = true)
class AnalyzeMethodBeanDefine extends AnalyzeBeanDefine {

    private String invokeBeanName;

    private Method defineMethod;

    public AnalyzeMethodBeanDefine(Class<?> beanType, Registration registration, Method defineMethod, String invokeBeanName) {
        super(beanType, registration, defineMethod);
        this.defineMethod = defineMethod;
        this.invokeBeanName = invokeBeanName;
    }
}
