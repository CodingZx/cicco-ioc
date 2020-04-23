package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.core.module.aop.InterceptorRegistry;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

class SingleBeanProvider extends AbstractBeanProvider {

    private final Object targetObject;
    private final Class<?>[] parameterTypes;
    private final Annotation[][] parameterAnnotations;

    SingleBeanProvider(InterceptorRegistry interceptorRegistry, BeanRegistry beanRegistry, AnalyzeBeanDefine beanDefine) {
        super(beanDefine.getBeanType(), beanRegistry, interceptorRegistry);
        this.parameterTypes = beanDefine.getParameterTypes();
        this.parameterAnnotations = beanDefine.getParameterAnnotations();
        // 创建实例
        this.targetObject = createProxy();
    }

    @Override
    public Object getObject() {
        return targetObject;
    }

    @Override
    public Class<?> superClass() {
        return originCls;
    }

    @Override
    public Class<?>[] interfaceClass() {
        return null;
    }

    @SneakyThrows
    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) {
        return proceed.invoke(self, args);
    }

    @Override
    public Class<?>[] getProxyParameterTypes() {
        return parameterTypes;
    }

    @Override
    public Annotation[][] getProxyParameterAnnotations() {
        return parameterAnnotations;
    }

    @Override
    public Map<Method, Annotation[]> filterBeanTypeMethods() {
        return analyzeMethods();
    }


    private Map<Method, Annotation[]> analyzeMethods() {
        Map<Method, Annotation[]> methodMap = new LinkedHashMap<>();
        for (Method method : originCls.getDeclaredMethods()) {

            Annotation[] annotations = method.getAnnotations();
            if (annotations == null || annotations.length == 0) {
                continue;
            }
            methodMap.put(method, annotations);
        }
        return methodMap;
    }
}
