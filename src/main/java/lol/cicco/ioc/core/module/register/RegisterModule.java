package lol.cicco.ioc.core.module.register;

import javassist.Modifier;
import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.InjectConstructor;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lol.cicco.ioc.core.module.aop.AnnotationInterceptor;
import lol.cicco.ioc.core.module.aop.AopModule;
import lol.cicco.ioc.core.module.aop.InterceptorRegistry;
import lol.cicco.ioc.core.module.beans.BeanModule;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lol.cicco.ioc.util.ClassUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

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

        LinkedList<AnalyzeBeanDefine> allRegister = new LinkedList<>();
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

                AnalyzeBeanDefine beanDefine = new AnalyzeBeanDefine();
                beanDefine.setBeanName(beanName);
                beanDefine.setParameterTypes(constructor.getParameterTypes());
                beanDefine.setParameterAnnotations(constructor.getParameterAnnotations());
                beanDefine.setBeanType(type);
                beanDefine.setCastClasses(ClassUtils.getClassTypes(type));
                allRegister.add(beanDefine);

                for (Method method : type.getDeclaredMethods()) {
                    Registration methodRegistration = method.getDeclaredAnnotation(Registration.class);
                    if (methodRegistration == null) {
                        continue;
                    }
                    String methodBeanName = "".equals(methodRegistration.name().trim()) ? method.getName() : methodRegistration.name().trim();

                    Class<?> methodBeanType = method.getReturnType();

                    AnalyzeMethodBeanDefine methodDefine = new AnalyzeMethodBeanDefine();
                    methodDefine.setBeanName(methodBeanName);
                    methodDefine.setParameterTypes(method.getParameterTypes());
                    methodDefine.setParameterAnnotations(method.getParameterAnnotations());
                    methodDefine.setBeanType(methodBeanType);
                    methodDefine.setCastClasses(ClassUtils.getClassTypes(methodBeanType));
                    methodDefine.setInvokeBeanName(beanName);
                    methodDefine.setDefineMethod(method);
                    allRegister.add(methodDefine);
                }
            }
        }
        analyzeBeanTypes(allRegister);
        // 注册至AOP
        registerAopInterceptor();
    }

    @SneakyThrows
    private void analyzeBeanTypes(List<AnalyzeBeanDefine> allRegister) {
        InterceptorRegistry interceptorRegistry = aopModule.getModuleProcessor();
        BeanRegistry beanRegistry = beanModule.getModuleProcessor();

        // 等待注册的Bean信息
        Map<String, AnalyzeBeanDefine> beans = new LinkedHashMap<>();
        Map<Class<?>, List<String>> types = new LinkedHashMap<>();

        for (AnalyzeBeanDefine define : allRegister) {
            beans.put(define.getBeanName(), define);
            for (Class<?> cls : define.getCastClasses()) {
                List<String> typeClassList = types.getOrDefault(cls, new LinkedList<>());
                typeClassList.add(define.getBeanName());
                types.put(cls, typeClassList);
            }
        }

        List<AnalyzeBeanDefine> waitInitQueue = new LinkedList<>();

        for (AnalyzeBeanDefine define : allRegister) {
            // 等待分析队列
            LinkedList<AnalyzeBeanDefine> waitAnalyzeDepends = new LinkedList<>();
            // 待初始化栈
            LinkedList<AnalyzeBeanDefine> registerStack = new LinkedList<>();
            waitAnalyzeDepends.add(define);

            while (!waitAnalyzeDepends.isEmpty()) {
                AnalyzeBeanDefine type = waitAnalyzeDepends.removeFirst();

                if (waitInitQueue.contains(type)) {
                    continue; // 已经放入初始化队列
                }
                if (registerStack.contains(type)) {
                    throw new RegisterException("检测到循环依赖... 请检查[" + type.getBeanType().getName() + "]依赖情况..");
                }

                Annotation[][] parameterAnnotations = type.getParameterAnnotations();
                Class<?>[] parameterTypes = type.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    List<String> dependType = types.get(parameterTypes[i]);
                    Inject injectAnnotation = parameterAnnotations[i] == null ? null : (Inject) Arrays.stream(parameterAnnotations[i]).filter(a -> a.annotationType().equals(Inject.class)).findFirst().orElse(null);

                    boolean require;
                    String injectName = null;
                    if (injectAnnotation == null) {
                        require = true;
                    } else {
                        require = injectAnnotation.required();
                        injectName = injectAnnotation.byName().trim();
                    }
                    if (dependType == null) {
                        if (require) {
                            throw new RegisterException("Class[" + type.getBeanType().getName() + "] 未找到注入类型[" + parameterTypes[i].getName() + "], 请检查构造参数是否正确..");
                        }
                    } else {
                        if ((injectName == null || injectName.equals("")) && dependType.size() != 1) {
                            throw new RegisterException("Class[" + type.getBeanType().getName() + "] 找到多个注入类型[" + parameterTypes[i].getName() + "], 请确认是否需要使用@Inject(byName=\"...\")指定名称注入..");
                        }
                        waitAnalyzeDepends.add(beans.get(dependType.get(0)));
                    }
                }
                registerStack.push(type);
            }
            // 依赖树分析完毕..
            while (!registerStack.isEmpty()) {
                waitInitQueue.add(registerStack.pop());
            }
        }

        for (AnalyzeBeanDefine define : waitInitQueue) {
            log.debug("Bean[{}]注册至IOC.", define.getBeanType().toString());
            if (define instanceof AnalyzeMethodBeanDefine) {
                beanRegistry.register(define.getBeanType(), define.getBeanName(), new MethodSingleBeanProvider(interceptorRegistry, beanRegistry, (AnalyzeMethodBeanDefine) define), false);
            } else {
                beanRegistry.register(define.getBeanType(), define.getBeanName(), new SingleBeanProvider(interceptorRegistry, beanRegistry, define), false);
            }

        }
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
