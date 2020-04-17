package lol.cicco.ioc.core.module.aop;

import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AopModule implements CiccoModule<AopProcessor> {
    public static final String AOP_MODULE_NAME = "_aopModule";
    private final AopProcessor processor = new AopProcessor();

    @Override
    public void initModule(CiccoContext context) {
        log.debug("init aop module.....");
    }

    @Override
    public String getModuleName() {
        return AOP_MODULE_NAME;
    }

    @Override
    public AopProcessor getModuleProcessor() {
        return processor;
    }

    @Override
    public List<String> dependOn() {
        return null;
    }
}
