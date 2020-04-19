package lol.cicco.ioc.mybatis;

import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lol.cicco.ioc.core.module.beans.BeanModule;
import lol.cicco.ioc.core.module.binder.BinderModule;
import lol.cicco.ioc.core.module.register.RegisterModule;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Slf4j
public class MybatisModule implements CiccoModule<Void> {
    public static final String MYBATIS_MODULE_NAME = "_mybatisModule";

    @Override
    public void initModule(CiccoContext context) {
        log.debug("init mybatis module...");
        BeanModule beanModule = (BeanModule) context.getModule(BeanModule.BEAN_MODULE_NAME);

        MybatisConfig config = (MybatisConfig) beanModule.getModuleProcessor().getNullableBean(MybatisConfig.class).getObject();
        var factory = config.createSqlFactory();

        MybatisConstants.factory = factory;

        Collection<Class<?>> mappers = factory.getConfiguration().getMapperRegistry().getMappers();

        for (Class<?> mapper : mappers) {
            log.debug("mapper is {}", mapper.getName());
            beanModule.getModuleProcessor().register(mapper, mapper.getName(), new MapperProvider(mapper), true);
        }
    }

    @Override
    public String getModuleName() {
        return MYBATIS_MODULE_NAME;
    }

    @Override
    public Void getModuleProcessor() {
        return null;
    }

    @Override
    public List<String> dependOn() {
        return Arrays.asList(BeanModule.BEAN_MODULE_NAME, BinderModule.BINDER_MODULE_NAME, RegisterModule.REGISTER_MODULE_NAME);
    }
}
