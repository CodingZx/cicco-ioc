package lol.cicco.ioc.core.module.aop;

import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AopModule implements CiccoModule<InterceptorRegistry>, InterceptorRegistry {
    public static final String AOP_MODULE_NAME = "_aopModule";

    private final Map<String, AnnotationInterceptor<?>> interceptorMap = new LinkedHashMap<>();

    @Override
    public void initModule(CiccoContext context) {
        log.debug("init aop module.....");
    }

    @Override
    public String getModuleName() {
        return AOP_MODULE_NAME;
    }

    @Override
    public InterceptorRegistry getModuleProcessor() {
        return this;
    }

    @Override
    public List<String> dependOn() {
        return null;
    }


    @Override
    public void register(AnnotationInterceptor<?> annotationInterceptor) {
        log.debug("aopProcessor register [{}]", annotationInterceptor.getAnnotation().getName());
        interceptorMap.put(annotationInterceptor.getAnnotation().getName(), annotationInterceptor);
    }

    @Override
    public AnnotationInterceptor<?> getInterceptor(Class<? extends Annotation> annotation) {
        return interceptorMap.get(annotation.getName());
    }

}
