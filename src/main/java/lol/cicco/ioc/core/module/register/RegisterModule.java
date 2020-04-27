package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lol.cicco.ioc.core.module.beans.BeanModule;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lol.cicco.ioc.core.module.interceptor.AnnotationInterceptor;
import lol.cicco.ioc.core.module.interceptor.InterceptorModule;
import lol.cicco.ioc.core.module.interceptor.InterceptorRegistry;
import lol.cicco.ioc.core.module.property.PropertyHandler;
import lol.cicco.ioc.core.module.property.PropertyModule;
import lol.cicco.ioc.core.module.property.PropertyRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class RegisterModule implements CiccoModule<Void> {
    public static final String REGISTER_MODULE_NAME = "_registerModule";

    private BeanRegistry beanRegistry;
    private InterceptorRegistry interceptorRegistry;
    private PropertyModule propertyModule;

    @Override
    public void initModule(CiccoContext context) {
        this.beanRegistry = (BeanModule) context.getModule(BeanModule.BEAN_MODULE_NAME).getModuleProcessor();
        this.interceptorRegistry = (InterceptorModule) context.getModule(InterceptorModule.INTERCEPTOR_MODULE).getModuleProcessor();
        this.propertyModule = (PropertyModule) context.getModule(PropertyModule.PROPERTY_MODULE_NAME);

        // 注册至BeanRegistry
        RegisterProcessor processor = new RegisterProcessor(beanRegistry, interceptorRegistry);
        processor.doRegister(context.getInitialize().getScanPackages());

        // 注册至Interceptor
        registerInterceptor();
        // 注册至PropertyHandler
        registerPropertyHandler();
        log.debug("init register module....");
    }

    @Override
    public String getModuleName() {
        return REGISTER_MODULE_NAME;
    }

    @Override
    public Void getModuleProcessor() {
        return null;
    }

    @Override
    public List<String> dependOn() {
        return Arrays.asList(BeanModule.BEAN_MODULE_NAME, InterceptorModule.INTERCEPTOR_MODULE, PropertyModule.PROPERTY_MODULE_NAME);
    }

    private void registerInterceptor() {
        Set<BeanProvider> interceptorProviders = beanRegistry.getNullableBeans(AnnotationInterceptor.class);
        if (interceptorProviders == null) {
            return;
        }
        for (BeanProvider provider : interceptorProviders) {
            interceptorRegistry.register((AnnotationInterceptor<?>) provider.getObject());
        }
    }

    private void registerPropertyHandler() {
        PropertyRegistry registry = propertyModule.getModuleProcessor();
        Set<BeanProvider> propertyBeanProvider = beanRegistry.getNullableBeans(PropertyHandler.class);
        if(propertyBeanProvider == null) {
            return;
        }
        for(BeanProvider provider : propertyBeanProvider) {
            registry.registerHandler((PropertyHandler<?>) provider.getObject());
        }
    }

}
