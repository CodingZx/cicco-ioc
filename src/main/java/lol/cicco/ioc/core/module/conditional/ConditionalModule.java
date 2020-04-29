package lol.cicco.ioc.core.module.conditional;

import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lol.cicco.ioc.core.module.beans.BeanModule;
import lol.cicco.ioc.core.module.property.PropertyModule;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConditionalModule implements CiccoModule<ConditionalRegistry>, ConditionalRegistry {
    public static final String CONDITIONAL_MODULE_NAME = "_conditionalModule";

    private final Map<Class<? extends Annotation>, ConditionalProcessor<?>> registerMap = new LinkedHashMap<>();

    @Override
    public void initModule(CiccoContext context) {
        BeanModule beanModule = (BeanModule) context.getModule(BeanModule.BEAN_MODULE_NAME);

        // 注册默认处理器
        register(new OnMissBeanTypeProcessor(beanModule.getModuleProcessor()));

        log.debug("init conditional module...");
    }

    @Override
    public String getModuleName() {
        return CONDITIONAL_MODULE_NAME;
    }

    @Override
    public ConditionalRegistry getModuleProcessor() {
        return this;
    }

    @Override
    public List<String> dependOn() {
        return Arrays.asList(BeanModule.BEAN_MODULE_NAME, PropertyModule.PROPERTY_MODULE_NAME);
    }

    @Override
    public void register(ConditionalProcessor<? extends Annotation> processor) {
        synchronized (registerMap) {
            registerMap.put(processor.getAnnotationType(), processor);
        }
    }

    @Override
    public boolean hasConditionalAnnotation(ConditionalBeanDefine beanDefine) {
        for (Annotation beanRegisterAnnotation : beanDefine.beanRegisterAnnotations()) {
            if (registerMap.containsKey(beanRegisterAnnotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkConditional(ConditionalBeanDefine beanType) {
        boolean flag = true;
        for (ConditionalProcessor<?> processor : registerMap.values()) {
            if (!processor.checkConditional(beanType)) {
                flag = false;
            }
        }
        return flag;
    }
}
