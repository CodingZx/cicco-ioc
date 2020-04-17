package lol.cicco.ioc.core;

import lol.cicco.ioc.core.module.property.PropertyHandler;

import java.util.Map;
import java.util.Set;

public interface Initialize {
    /**
     * 设置IOC扫描包名
     */
    Initialize registerPropertyHandler(PropertyHandler<?> propertyHandler);

    /**
     * 获得已设置的属性转换器
     */
    Set<PropertyHandler<?>> getPropertyHandlers();

    /**
     * 设置IOC加载配置文件
     */
    Initialize loadProperties(String... propertyFiles);

    /**
     * 获得已加载的配置文件
     */
    Set<String> getLoadPropertyFiles();

    /**
     * 设置属性转换器
     */
    Initialize scanBasePackages(String... packages);

    /**
     * 获得已设置的ScanPackage
     */
    Set<String> getScanPackages();

    /**
     * 设置处理模块
     */
    Initialize registerModule(CiccoModule<?> module);

    /**
     * 获取已注册的处理模块
     */
    Map<String, CiccoModule<?>> getModules();

    /**
     * 初始化属性配置完毕
     */
    void done();
}
