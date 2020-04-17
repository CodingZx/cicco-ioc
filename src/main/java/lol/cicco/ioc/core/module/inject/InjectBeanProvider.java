package lol.cicco.ioc.core.module.inject;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

class InjectBeanProvider implements BeanProvider {
    private final BeanProvider beanProvider;
    private final BeanRegistry beanRegistry;
    private Object targetObj = null;

    InjectBeanProvider(BeanProvider beanProvider, BeanRegistry beanRegistry) {
        this.beanProvider = beanProvider;
        this.beanRegistry = beanRegistry;
    }

    @Override
    public Class<?> beanType() {
        return beanProvider.beanType();
    }

    @Override
    @SneakyThrows
    public Object getObject() {
        Object oldObj = beanProvider.getObject();
        if(oldObj == targetObj) { // 相同代表同一个对象, 执行过注入
            return targetObj;
        }
        targetObj = oldObj;
        Field[] fields = beanProvider.beanType().getDeclaredFields();
        for (Field field : fields) {
            Inject inject = field.getAnnotation(Inject.class);
            if (inject == null) {
                continue;
            }
            if (!field.canAccess(targetObj)) {
                field.setAccessible(true);
            }

            BeanProvider injectProvider;
            if (!"".equals(inject.byName().trim())) {
                injectProvider = beanRegistry.getNullableBean(inject.byName().trim());
            } else {
                injectProvider = beanRegistry.getNullableBean(field.getType());
            }

            if (inject.required()) {
                if (injectProvider == null) {
                    throw new InjectException("[" + field.getType().getTypeName() + "] 未注册至IOC, 请检查[" + Registration.class + "]注解与初始化配置.");
                }
            }

            if (injectProvider != null) {
                field.set(targetObj, injectProvider.getObject());
            }
        }
        return targetObj;
    }
}
