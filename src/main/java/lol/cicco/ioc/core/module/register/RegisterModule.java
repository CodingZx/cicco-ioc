package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lol.cicco.ioc.core.IOC;
import lol.cicco.ioc.core.module.aop.AopModule;
import lol.cicco.ioc.core.module.aop.Interceptor;
import lol.cicco.ioc.core.module.aop.InterceptorRegistry;
import lol.cicco.ioc.core.module.beans.BeanModule;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class RegisterModule implements CiccoModule<Void> {
    public static final String REGISTER_MODULE_NAME = "_registerModule";

    private BeanModule beanModule;
    private AopModule aopModule;


    @Override
    public void initModule(CiccoContext context) {
        this.beanModule = (BeanModule) context.getModule(BeanModule.BEAN_MODULE_NAME);
        this.aopModule = (AopModule) context.getModule(AopModule.AOP_MODULE_NAME);

        registerBeans(context.getInitialize().getScanPackages());
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
        return Arrays.asList(BeanModule.BEAN_MODULE_NAME, AopModule.AOP_MODULE_NAME);
    }

    private void registerBeans(Set<String> packages) {
        InterceptorRegistry registry = aopModule.getModuleProcessor();

        ClassResourceScanner scanner = new ClassResourceScanner();
        for (String pkg : packages) {
            Set<ClassResourceMeta> classResourceMetas = scanner.scanClassMeta(pkg, IOC.class.getClassLoader());
            for (ClassResourceMeta meta : classResourceMetas) {
                Class<?> type = meta.getSelfType();

                Registration registration = type.getDeclaredAnnotation(Registration.class);
                if (registration == null) {
                    continue;
                }

                try {
                    type.getConstructor(); // 校验是否存在默认构造函数
                } catch (NoSuchMethodException e) {
                    throw new RegisterException("[" + type.toString() + "] 需要使用默认构造函数....", e);
                }

                String beanName = "".equals(registration.name().trim()) ? meta.getSelfType().getName() : registration.name().trim();

                log.debug("Bean[{}]注册至IOC. Path[{}]", meta.getSelfType().toString(), meta.getFilePath());
                beanModule.register(type, beanName, new SingleBeanProvider(meta.getSelfType(), registry), false);
            }
        }
        // 注册至AOP
        registerAopInterceptor();
    }

    private void registerAopInterceptor() {
        InterceptorRegistry interceptorRegistry = aopModule.getModuleProcessor();

        Set<BeanProvider> interceptorProviders = beanModule.getModuleProcessor().getNullableBeans(Interceptor.class);
        if(interceptorProviders == null) {
            return;
        }
        for(BeanProvider provider : interceptorProviders) {
            interceptorRegistry.register((Interceptor<?>) provider.getObject());
        }
    }

}
