package lol.cicco.ioc.core.module.binder;

import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lol.cicco.ioc.core.module.beans.BeanModule;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lol.cicco.ioc.core.module.property.PropertyModule;
import lol.cicco.ioc.core.module.property.PropertyRegistry;
import lol.cicco.ioc.core.module.register.RegisterModule;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class BinderModule implements CiccoModule<Void> {
    public static final String BINDER_MODULE_NAME = "_binderModule";

    private BeanModule beanModule;
    private PropertyModule propertyModule;

    @Override
    public void initModule(CiccoContext context) {
        beanModule = (BeanModule) context.getModule(BeanModule.BEAN_MODULE_NAME);
        propertyModule = (PropertyModule) context.getModule(PropertyModule.PROPERTY_MODULE_NAME);

        doBinder();

        log.debug("init binder module.....");
    }

    @Override
    public String getModuleName() {
        return BINDER_MODULE_NAME;
    }

    @Override
    public Void getModuleProcessor() {
        return null;
    }

    @Override
    public List<String> dependModule() {
        return Arrays.asList(BeanModule.BEAN_MODULE_NAME, RegisterModule.REGISTER_MODULE_NAME, PropertyModule.PROPERTY_MODULE_NAME);
    }

    @Override
    public List<String> afterModule() {
        return null;
    }

    private void doBinder() {
        PropertyRegistry propertyRegistry = propertyModule.getModuleProcessor();
        BeanRegistry beanRegistry = beanModule.getModuleProcessor();

        for (String beanName : beanRegistry.getRegisterBeans()) {
            BeanProvider provider = beanRegistry.getNullableBean(beanName);

            log.debug("执行依赖注入, 当前目标:{}", provider.beanType().toString());

            beanRegistry.register(provider.beanType(), beanName, new BinderBeanProvider(provider, propertyRegistry), true);
        }
    }
}
