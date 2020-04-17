package lol.cicco.ioc.core;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CiccoContext {
    private final Initialize initialize;

    public CiccoContext(Initialize initialize) {
        this.initialize = initialize;

        initModule();
    }

    private void initModule() {
        LinkedList<CiccoModule<?>> waitInitModules = new LinkedList<>();
        Set<String> alreadyInit = new HashSet<>();

        Map<String, CiccoModule<?>> modules = initialize.getModules();

        for(String moduleName : modules.keySet()) {
            CiccoModule<?> module = modules.get(moduleName);

            waitInitModules.add(module);

            while(!waitInitModules.isEmpty()) {
                CiccoModule<?> tmp = waitInitModules.getLast();
                if(tmp == null) {
                    throw new CiccoModuleException("未找到依赖模块, 请检查模块是否注册至Context....");
                }
                if(alreadyInit.contains(tmp.getModuleName())) {
                    waitInitModules.removeLast();
                    continue;
                }
                if(tmp.dependOn() == null || tmp.dependOn().size() == 0) {
                    tmp.initModule(this);
                    alreadyInit.add(tmp.getModuleName());
                    waitInitModules.removeLast();
                    continue;
                }
                boolean canInit = true;
                for(String depend : tmp.dependOn()) {
                    if(!alreadyInit.contains(depend)) {
                        canInit = false;
                    } else {
                        continue;
                    }
                    CiccoModule<?> dependModule = modules.get(depend);
                    if(waitInitModules.contains(dependModule)) {
                        // 循环依赖
                        throw new CiccoModuleException("循环依赖..请检查{" + dependModule.getModuleName()+"}依赖情况..");
                    }
                    waitInitModules.add(dependModule);
                }

                if(canInit) {
                    waitInitModules.remove(tmp);
                    alreadyInit.add(tmp.getModuleName());
                    tmp.initModule(this);
                }
            }
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
