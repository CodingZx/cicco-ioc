package lol.cicco.ioc.core.module.register;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
class AnalyzeMethodBeanDefine extends AnalyzeBeanDefine {

    private String invokeBeanName;

    private Method defineMethod;
}
