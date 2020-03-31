package lol.cicco.ioc.core;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class Initialize {

    private Set<String> scanPackages;
    private Set<String> loadProperties;

    public Initialize() {
        scanPackages = new LinkedHashSet<>();
        loadProperties = new LinkedHashSet<>();
    }

    public Initialize scanBasePackages(String... packages) {
        scanPackages.addAll(Arrays.asList(packages));
        return this;
    }

    public Initialize scanBasePackageClasses(Class<?>... classes) {
        for (Class<?> cls : classes) {
            scanPackages.add(cls.getPackageName());
        }
        return this;
    }

    public Initialize loadProperties(String... propertyNames) {
        for(String propertyName : propertyNames) {
            propertyName = propertyName.trim();
            if(propertyName.startsWith("/")) {
                loadProperties.add(propertyName);
            } else {
                loadProperties.add("/" + propertyName);
            }
        }
        return this;
    }

    public void done() {
        // 初始化完成
        IOC.initializeDone(this);
    }

    Set<String> getScanPackages() {
        return this.scanPackages;
    }

    Set<String> getLoadProperties(){
        return this.loadProperties;
    }
}
