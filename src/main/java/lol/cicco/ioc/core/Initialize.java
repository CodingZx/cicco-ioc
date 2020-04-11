package lol.cicco.ioc.core;

import lol.cicco.ioc.core.aop.Interceptor;
import lol.cicco.ioc.core.binder.PropertyHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.*;

@Slf4j
public class Initialize {

    private final Set<String> scanPackages;
    private final Set<String> loadPropertyFiles;

    private final List<PropertyHandler<?>> propertyHandlers;

    private final Map<Class<? extends Annotation>, Interceptor> interceptors;

    public Initialize() {
        scanPackages = new LinkedHashSet<>();
        loadPropertyFiles = new LinkedHashSet<>();
        propertyHandlers = new LinkedList<>();
        interceptors = new LinkedHashMap<>();
    }

    /**
     * 设置IOC扫描包名
     */
    public Initialize scanBasePackages(String... packages) {
        scanPackages.addAll(Arrays.asList(packages));
        return this;
    }

    /**
     * 设置IOC扫描Class所在包
     */
    public Initialize scanBasePackageClasses(Class<?>... classes) {
        for (Class<?> cls : classes) {
            scanPackages.add(cls.getPackageName());
        }
        return this;
    }

    /**
     * 设置IOC加载配置文件
     */
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
     * 设置属性转换器
     */
    public Initialize registerPropertyHandler(PropertyHandler<?> propertyHandler) {
        propertyHandlers.add(propertyHandler);
        return this;
    }

    /**
     * 设置注解拦截器
     */
    public Initialize registerInterceptor(Class<? extends Annotation> annotation, Interceptor interceptor) {
        interceptors.put(annotation, interceptor);
        return this;
    }

    /**
     * 初始化属性配置完毕
     */
    public void done() {
        // 初始化完成
        IOC.initializeDone(this);
    }

    /**
     * 获得已设置的ScanPackage
     */
    Set<String> getScanPackages() {
        return this.scanPackages;
    }

    /**
     * 获得已设置的配置文件
     */
    Set<String> getLoadPropertyFiles() {
        return this.loadPropertyFiles;
    }

    /**
     * 获得已设置的属性转换器
     */
    List<PropertyHandler<?>> getPropertyHandlers() {
        return this.propertyHandlers;
    }

    /**
     * 获得已设置的注解拦截器
     */
    Map<Class<? extends Annotation>, Interceptor> getInterceptors() {
        return this.interceptors;
    }
}
