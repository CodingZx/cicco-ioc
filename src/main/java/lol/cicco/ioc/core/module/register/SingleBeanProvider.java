package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lol.cicco.ioc.core.module.interceptor.InterceptorRegistry;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

class SingleBeanProvider extends AbstractBeanProvider implements InitializeBeanProvider {

    private final Class<?>[] parameterTypes;
    private final Annotation[][] parameterAnnotations;
    private Object targetObject;

    SingleBeanProvider(InterceptorRegistry interceptorRegistry, BeanRegistry beanRegistry, AnalyzeBeanDefine beanDefine) {
        super(beanDefine.getBeanType(), beanRegistry, interceptorRegistry);
        this.parameterTypes = beanDefine.getParameterTypes();
        this.parameterAnnotations = beanDefine.getParameterAnnotations();
    }

    @Override
    @SneakyThrows
    public Object getObject() {
        initialize();
        return targetObject;
    }

    @Override
    public boolean proxyFactoryCache() {
        return false;
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

    @Override
    public void initialize() throws Exception {
        if (targetObject != null) {
            // 已经初始化过
            return;
        }
        if (depends.contains(this)) {
            throw new RegisterException("检测到循环依赖... 请检查[" + beanType().getName() + "]依赖情况..");
        }
        depends.add(this);
        this.targetObject = createProxy();
        depends.remove(this);
    }

    @Override
    public BeanProvider getBeanProvider() {
        return this;
    }
}
