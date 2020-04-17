package lol.cicco.ioc.core.module.inject;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lol.cicco.ioc.core.BeanNotFountException;
import lol.cicco.ioc.core.module.beans.BeanModule;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.core.module.beans.BeanRegistry;
import lol.cicco.ioc.core.module.register.RegisterModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class InjectModule implements CiccoModule<Void> {
    public static final String INJECT_MODULE_NAME = "_injectModule";

    private BeanModule beanModule;

    @Override
    public void initModule(CiccoContext context) {
        this.beanModule = (BeanModule)context.getModule(BeanModule.BEAN_MODULE_NAME);

        doInject();
        log.debug("init inject module....");
    }

    @Override
    public String getModuleName() {
        return INJECT_MODULE_NAME;
    }

    @Override
    public Void getModuleProcessor() {
        return null;
    }

    @Override
    public List<String> dependOn() {
        return Arrays.asList(BeanModule.BEAN_MODULE_NAME, RegisterModule.REGISTER_MODULE_NAME);
    }

    @SneakyThrows
    private void doInject() {
        BeanRegistry beanRegistry = beanModule.getModuleProcessor();

        for (String beanName : beanRegistry.getRegisterBeans()) {
            BeanProvider provider = beanRegistry.getNullableBean(beanName);

            log.debug("执行依赖注入, 当前目标:{}", provider.beanType().toString());

            beanRegistry.register(provider.beanType(), beanName, new InjectBeanProvider(provider, beanRegistry), true);
        }
    }
}
