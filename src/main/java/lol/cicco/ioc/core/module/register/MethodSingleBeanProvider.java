package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lol.cicco.ioc.core.module.interceptor.InterceptorRegistry;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

class MethodSingleBeanProvider extends AbstractBeanProvider implements InitializeBeanProvider {

    private final boolean interfaceDefine;
    private final Object originObj;
    private Object proxyTarget;

    @SneakyThrows
    MethodSingleBeanProvider(InterceptorRegistry interceptorRegistry, BeanRegistry beanRegistry, AnalyzeMethodBeanDefine methodBeanDefine) {
        super(methodBeanDefine.getBeanType(), beanRegistry, interceptorRegistry);
        this.interfaceDefine = Modifier.isInterface(methodBeanDefine.getBeanType().getModifiers());

        BeanProvider provider = beanRegistry.getNullableBean(methodBeanDefine.getInvokeBeanName());
        Object[] dependBeans = getParams(methodBeanDefine.getParameterTypes(), methodBeanDefine.getParameterAnnotations());
        // invoke 执行后拿到方法生成的原始类对象
        originObj = methodBeanDefine.getDefineMethod().invoke(provider.getObject(), dependBeans);
    }

    @Override
    public boolean proxyFactoryCache() {
        return false;
    }

    @Override
    public Class<?> superClass() {
        return interfaceDefine ? null : originCls;
    }

    @Override
    public Class<?>[] interfaceClass() {
        return interfaceDefine ? new Class[]{originCls} : null;
    }

    @Override
    @SneakyThrows
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) {
        Method proxyMethod = originCls.getDeclaredMethod(thisMethod.getName(), thisMethod.getParameterTypes());
        return proxyMethod.invoke(originObj, args);
    }

    @Override
    public Class<?>[] getProxyParameterTypes() {
        return new Class[0];
    }

    @Override
    public Annotation[][] getProxyParameterAnnotations() {
        return new Annotation[0][0];
    }

    @Override
    @SneakyThrows
    public Object getObject() {
        initialize();
        return proxyTarget;
    }

    @Override
    public Map<Method, Annotation[]> filterBeanTypeMethods() {
        return analyzeMethods(originObj);
    }

    @SneakyThrows
    private Map<Method, Annotation[]> analyzeMethods(Object originObj) {
        Map<Method, Annotation[]> methodMap = new LinkedHashMap<>();
        for (Method method : originCls.getDeclaredMethods()) {
            Method implMethod = originObj.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
            Annotation[] annotations = implMethod.getDeclaredAnnotations();
            Annotation[] originAnnotations = method.getDeclaredAnnotations();
            if (annotations.length == 0 && originAnnotations.length == 0) {
                continue;
            }
            Set<Annotation> annotationSet = new HashSet<>();
            annotationSet.addAll(Arrays.asList(annotations));
            annotationSet.addAll(Arrays.asList(originAnnotations));
            methodMap.put(method, annotationSet.toArray(new Annotation[0]));
        }
        return methodMap;
    }

    @Override
    public void initialize() throws Exception {
        if (proxyTarget != null) {
            return;
        }
        if (depends.contains(this)) {
            throw new RegisterException("检测到循环依赖... 请检查[" + beanType().getName() + "]依赖情况..");
        }
        depends.add(this);
        proxyTarget = createProxy();
        depends.remove(this);
    }

    @Override
    public BeanProvider getBeanProvider() {
        return this;
    }
}
