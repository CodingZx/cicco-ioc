package lol.cicco.ioc.core;

import java.util.List;

public interface CiccoModule<T> {
    /**
     * 初始化模块
     */
    void initModule(CiccoContext context);

    /**
     * 获取模块名称
     */
    String getModuleName();

    /**
     * 获取模块对应处理器
     */
    T getModuleProcessor();

    /**
     * 定义依赖模块, 给定模块初始化后再执行初始化
     */
    List<String> dependModule();

    /**
     * 保证此模块一定会在给定模块之前初始化
     */
    List<String> afterModule();

}
