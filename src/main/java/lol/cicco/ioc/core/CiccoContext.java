package lol.cicco.ioc.core;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@Slf4j
public class CiccoContext {
    private final Initialize initialize;

    public CiccoContext(Initialize initialize) {
        this.initialize = initialize;

        Stack<String> waitInitModules = new Stack<>();
        Set<String> alreadyInit = new HashSet<>();

        Map<String, CiccoModule<?>> modules = initialize.getModules();
        for (String moduleName : modules.keySet()) {
            if(alreadyInit.contains(moduleName)) {
                continue; // 已经初始化
            }
            CiccoModule<?> module = modules.get(moduleName);
            if (module.dependOn() == null || module.dependOn().size() == 0) {
                module.initModule(this);
                alreadyInit.add(moduleName);
            } else {
                for (String dependModuleName : module.dependOn()) {
                    if (alreadyInit.contains(dependModuleName)) {
                        continue;
                    }
                    if(waitInitModules.contains(moduleName)) {
                        throw new RuntimeException(); // 循环依赖
                    }
                    waitInitModules.push(moduleName);
                }
                while(!waitInitModules.isEmpty()){
                    String waitName = waitInitModules.pop();
                    modules.get(waitName).initModule(this);
                    alreadyInit.add(waitName);
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
