package lol.cicco.ioc.core;

import lol.cicco.ioc.core.module.beans.BeanModule;
import lol.cicco.ioc.core.module.binder.BinderModule;
import lol.cicco.ioc.core.module.condition.ConditionRegistryImpl;
import lol.cicco.ioc.core.module.inject.InjectModule;
import lol.cicco.ioc.core.module.interceptor.InterceptorModule;
import lol.cicco.ioc.core.module.property.PropertyModule;
import lol.cicco.ioc.core.module.register.RegisterModule;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DefaultInitialize implements Initialize {

    private final Set<String> scanPackages;
    private final Set<String> loadPropertyFiles;

    private final Map<String, CiccoModule<?>> ciccoModules;

    public DefaultInitialize() {
        scanPackages = new LinkedHashSet<>();
        loadPropertyFiles = new LinkedHashSet<>();
        ciccoModules = new LinkedHashMap<>();

        // 初始化默认模块
        registerModule(new PropertyModule());
        registerModule(new InterceptorModule());
        registerModule(new BeanModule());
        registerModule(new RegisterModule());
        registerModule(new InjectModule());
        registerModule(new BinderModule());
        registerModule(new ConditionRegistryImpl());
    }

    /**
     * 设置IOC扫描包名
     */
    @Override
    public Initialize scanBasePackages(String... packages) {
        scanPackages.addAll(Arrays.asList(packages));
        return this;
    }

    /**
     * 设置IOC加载配置文件
     */
    @Override
    public Initialize loadProperties(String... propertyFiles) {
        for (String propertyName : propertyFiles) {
            propertyName = propertyName.trim();
            if (propertyName.startsWith("/")) {
                loadPropertyFiles.add(propertyName);
            } else {
                loadPropertyFiles.add("/" + propertyName);
            }
        }
        return this;
    }

    /**
     * 初始化属性配置完毕
     */
    @Override
    public void done() {
        // 初始化完成
        IOC.initializeDone(this);
        System.gc();
    }

    /**
     * 获得已设置的ScanPackage
     */
    @Override
    public Set<String> getScanPackages() {
        return this.scanPackages;
    }

    /**
     * 设置注解处理模块
     */
    @Override
    public Initialize registerModule(CiccoModule<?> module) {
        ciccoModules.put(module.getModuleName(), module);
        return this;
    }

    /**
     * 获取已注册的注解处理模块
     */
    @Override
    public Map<String, CiccoModule<?>> getModules() {
        return ciccoModules;
    }

    /**
     * 获得已设置的配置文件
     */
    @Override
    public Set<String> getLoadPropertyFiles() {
        return this.loadPropertyFiles;
    }

}
