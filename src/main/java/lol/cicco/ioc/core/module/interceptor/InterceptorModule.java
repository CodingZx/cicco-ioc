package lol.cicco.ioc.core.module.interceptor;

import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lol.cicco.ioc.core.module.beans.BeanModule;
import lol.cicco.ioc.core.module.register.RegisterModule;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class InterceptorModule implements CiccoModule<InterceptorRegistry>, InterceptorRegistry {
    public static final String INTERCEPTOR_MODULE = "_interceptorModule";

    private final Map<String, AnnotationInterceptor<?>> interceptorMap = new LinkedHashMap<>();

    @Override
    public void initModule(CiccoContext context) {
        log.debug("init interceptor module.....");
    }

    @Override
    public String getModuleName() {
        return INTERCEPTOR_MODULE;
    }

    @Override
    public InterceptorRegistry getModuleProcessor() {
        return this;
    }

    @Override
    public List<String> dependModule() {
        return Collections.singletonList(BeanModule.BEAN_MODULE_NAME);
    }

    @Override
    public List<String> afterModule() {
        return Collections.singletonList(RegisterModule.REGISTER_MODULE_NAME);
    }


    @Override
    public void register(AnnotationInterceptor<?> annotationInterceptor) {
        log.debug("InterceptorRegistry register [{}]", annotationInterceptor.getAnnotation().getName());
        interceptorMap.put(annotationInterceptor.getAnnotation().getName(), annotationInterceptor);
    }

    @Override
    public AnnotationInterceptor<?> getInterceptor(Class<? extends Annotation> annotation) {
        return interceptorMap.get(annotation.getName());
    }

}
