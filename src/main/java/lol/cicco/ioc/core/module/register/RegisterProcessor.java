package lol.cicco.ioc.core.module.register;

import javassist.Modifier;
import lol.cicco.ioc.annotation.InjectConstructor;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lol.cicco.ioc.core.module.conditional.ConditionalRegistry;
import lol.cicco.ioc.core.module.interceptor.InterceptorRegistry;
import lol.cicco.ioc.core.module.scan.ResourceScanner;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@Slf4j
class RegisterProcessor {

    private final BeanRegistry beanRegistry;
    private final InterceptorRegistry interceptorRegistry;
    private final ConditionalRegistry conditionalRegistry;
    private final ResourceScanner resourceScanner;


    public RegisterProcessor(BeanRegistry beanRegistry, InterceptorRegistry interceptorRegistry, ConditionalRegistry conditionalRegistry, ResourceScanner scanner) {
        this.beanRegistry = beanRegistry;
        this.interceptorRegistry = interceptorRegistry;
        this.conditionalRegistry = conditionalRegistry;
        this.resourceScanner = scanner;
    }

    public void doRegister(Set<String> packages) {
        // 扫描Class
        ClassResourceScanner scanner = new ClassResourceScanner(resourceScanner);

        LinkedList<AnalyzeBeanDefine> analyzeBeanDefines = new LinkedList<>();
        for (String pkg : packages) {
            Set<ClassResourceMeta> classResourceMetas = scanner.scanClassMeta(pkg, RegisterModule.class.getClassLoader());
            // 查找依赖
            for (ClassResourceMeta meta : classResourceMetas) {
                Class<?> type = meta.getSelfType();
                if (Modifier.isInterface(type.getModifiers()) || Modifier.isAbstract(type.getModifiers())) {
                    continue; // 非注册类
                }
                Registration beanRegistration = type.getDeclaredAnnotation(Registration.class);
                if (beanRegistration == null) {
                    continue;
                }
                String beanName = "".equals(beanRegistration.name().trim()) ? type.getName() : beanRegistration.name().trim();
                Constructor<?> constructor = analyzeBeanConstructor(type);

                analyzeBeanDefines.add(new AnalyzeBeanDefine(type, beanRegistration, constructor, type.getDeclaredAnnotations()));

                for (Method method : type.getDeclaredMethods()) {
                    Registration methodRegistration = method.getDeclaredAnnotation(Registration.class);
                    if (methodRegistration == null) {
                        continue;
                    }
                    Class<?> methodBeanType = method.getReturnType();
                    analyzeBeanDefines.add(new AnalyzeMethodBeanDefine(methodBeanType, methodRegistration, method, beanName, method.getDeclaredAnnotations()));
                }
            }
        }

        registerAllBeanProvider(analyzeBeanDefines);
    }

    private void registerAllBeanProvider(List<AnalyzeBeanDefine> analyzeBeanDefines) {
        Queue<InitializeBeanProvider> waitInitializeProviderQueue = new LinkedList<>();
        Queue<AnalyzeBeanDefine> conditionalBeans = new LinkedList<>();

        for (AnalyzeBeanDefine beanDefine : analyzeBeanDefines) {
            // 如果为Conditional Bean则先放起来 等其他类注册完毕后再进行判断
            if(conditionalRegistry.hasConditionalAnnotation(beanDefine)) {
                conditionalBeans.add(beanDefine);
                continue;
            }
            // 注册
            waitInitializeProviderQueue.add(registerBeanProvider(beanDefine));
        }

        // 校验Conditional
        while(!conditionalBeans.isEmpty()){
            AnalyzeBeanDefine beanDefine = conditionalBeans.poll();
            if(conditionalRegistry.checkConditional(beanDefine)) {
                waitInitializeProviderQueue.add(registerBeanProvider(beanDefine));
            }
        }

        for (InitializeBeanProvider bean : waitInitializeProviderQueue) {
            try {
                bean.initialize();
            } catch (Exception e) {
                throw new RegisterException("初始化异常: " + e.getMessage(), e);
            }
        }
    }

    private InitializeBeanProvider registerBeanProvider(AnalyzeBeanDefine beanDefine) {
        InitializeBeanProvider initializeBean;
        if (beanDefine instanceof AnalyzeMethodBeanDefine) {
            initializeBean = new MethodSingleBeanProvider(interceptorRegistry, beanRegistry, (AnalyzeMethodBeanDefine) beanDefine);
        } else {
            initializeBean = new SingleBeanProvider(interceptorRegistry, beanRegistry, beanDefine);
        }
        // 校验BeanName是否重复
        if (beanRegistry.containsBean(beanDefine.getBeanName())) {
            throw new IllegalStateException("BeanName[" + beanDefine.getBeanName() + "] 被重复定义, 对应Class [" + beanDefine.getBeanType().getName() + ", " + beanRegistry.getNullableBean(beanDefine.getBeanName()).beanType().getName() + "]..");
        }

        log.debug("Bean[{}]注册至IOC..", beanDefine.getBeanType().toString());
        beanRegistry.register(beanDefine.getBeanType(), beanDefine.getBeanName(), initializeBean.getBeanProvider());

        return initializeBean;
    }

    /**
     * 分析构造函数
     */
    private Constructor<?> analyzeBeanConstructor(Class<?> bean) {
        Constructor<?>[] constructors = bean.getConstructors();
        if (constructors.length == 1) {
            return constructors[0];
        }
        Constructor<?> useConstructor = null;
        for (Constructor<?> constructor : constructors) {
            if (constructor.getDeclaredAnnotation(InjectConstructor.class) != null) {
                if (useConstructor != null) {
                    throw new RegisterException("[" + bean.toString() + "] 无法使用多个构造函数....无法初始化... 请在确认使用的构造函数上添加@InjectConstructor");
                }
                useConstructor = constructor;
            }
        }
        if (useConstructor != null) {
            return useConstructor;
        }
        throw new RegisterException("[" + bean.toString() + "] 拥有多个构造函数....无法初始化...");
    }
}
