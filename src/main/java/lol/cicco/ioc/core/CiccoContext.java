package lol.cicco.ioc.core;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CiccoContext {
    private final Initialize initialize;

    public CiccoContext(Initialize initialize) {
        this.initialize = initialize;

        initModule();
    }

    private void initModule() {
        DependGraph dependGraph = new DependGraph(initialize.getModules());

        List<CiccoModule<?>> sortList = dependGraph.sort();

        for(CiccoModule<?> module : sortList) {
            System.out.println(module.getModuleName());
        }
        for(CiccoModule<?> module : sortList) {
            module.initModule(this);
        }
    }

    /**
     * 获取初始化相关配置参数
     */
    public Initialize getInitialize() {
        return initialize;
    }

    /**
     * 获取注册模块
     */
    public CiccoModule<?> getModule(String moduleName) {
        return initialize.getModules().get(moduleName);
    }
}
