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
     * 获取初始化顺序
     */
    List<String> dependOn();
}
