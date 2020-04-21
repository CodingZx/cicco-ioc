package lol.cicco.ioc.core.module.register;

import javassist.Modifier;
import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lol.cicco.ioc.core.module.aop.AnnotationInterceptor;
import lol.cicco.ioc.core.module.aop.AopModule;
import lol.cicco.ioc.core.module.aop.InterceptorRegistry;
import lol.cicco.ioc.core.module.beans.BeanModule;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedList;
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
        ClassResourceScanner scanner = new ClassResourceScanner();
        for (String pkg : packages) {
            Set<ClassResourceMeta> classResourceMetas = scanner.scanClassMeta(pkg, RegisterModule.class.getClassLoader());
            // 查找依赖
            for (ClassResourceMeta meta : classResourceMetas) {
                analyzeBeanType(meta);
            }
        }
        // 注册至AOP
        registerAopInterceptor();
    }

    @SneakyThrows
    private void analyzeBeanType(ClassResourceMeta meta) {
        InterceptorRegistry interceptorRegistry = aopModule.getModuleProcessor();
        BeanRegistry beanRegistry = beanModule.getModuleProcessor();

        // 等待分析队列
        LinkedList<Class<?>> waitAnalyzeBeans = new LinkedList<>();
        waitAnalyzeBeans.add(meta.getSelfType());
        // 待初始化栈
        LinkedList<AnalyzeBeanDefine> registerStack = new LinkedList<>();
        while (!waitAnalyzeBeans.isEmpty()) {
            Class<?> type = waitAnalyzeBeans.removeLast();
            if(waitAnalyzeBeans.contains(type)) {
                throw new RegisterException("循环依赖... 请检查["+type.getName()+"]依赖情况..");
            }
            Registration registration = type.getDeclaredAnnotation(Registration.class);
            if (registration == null || Modifier.isInterface(type.getModifiers()) || Modifier.isAbstract(type.getModifiers())) {
                continue; // 非注册类
            }
            String beanName = "".equals(registration.name().trim()) ? meta.getSelfType().getName() : registration.name().trim();

            if (beanRegistry.containsBean(beanName)) {
                continue; //已初始化过
            }

            Constructor<?> constructor = analyzeBeanConstructor(type);
            registerStack.push(new AnalyzeBeanDefine(type, beanName, constructor));

            if (constructor.getParameterTypes().length > 0) {
                // 继续扫描依赖
                waitAnalyzeBeans.addAll(Arrays.asList(constructor.getParameterTypes()));
            }
        }

        while (!registerStack.isEmpty()) {
            AnalyzeBeanDefine define = registerStack.pop();

            log.debug("Bean[{}]注册至IOC. Path[{}]", meta.getSelfType().toString(), meta.getFilePath());
            BeanProvider beanProvider = new SingleBeanProvider(meta.getSelfType(), interceptorRegistry, beanRegistry, define.getBeanConstructor());
            beanRegistry.register(define.getBeanType(), define.getBeanName(), beanProvider, false);
        }
    }

    private Constructor<?> analyzeBeanConstructor(Class<?> bean) {
        Constructor<?>[] constructors = bean.getConstructors();
        if (constructors.length == 1) {
            return constructors[0];
        }
        throw new RegisterException("[" + bean.toString() + "] 拥有多个构造函数....无法初始化...");
    }

    private void registerAopInterceptor() {
        InterceptorRegistry interceptorRegistry = aopModule.getModuleProcessor();

        Set<BeanProvider> interceptorProviders = beanModule.getModuleProcessor().getNullableBeans(AnnotationInterceptor.class);
        if (interceptorProviders == null) {
            return;
        }
        for (BeanProvider provider : interceptorProviders) {
            interceptorRegistry.register((AnnotationInterceptor<?>) provider.getObject());
        }
    }

}
