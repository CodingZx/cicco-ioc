package lol.cicco.ioc.core;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class Initialize {

    private Set<String> scanPackages;
    private ClassPathScanner scanner;
    private Set<BeanDefinition> beanDefinitions;

    public Initialize() {
        scanPackages = new LinkedHashSet<>();
        scanner = new ClassPathScanner();
        beanDefinitions = new LinkedHashSet<>();
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

    public void done() {
        // 执行扫描
        for(String pkg : scanPackages) {
            beanDefinitions.addAll(scanner.doScan(pkg, Initialize.class.getClassLoader()));
        }

        // 初始化完成
        IOC.initializeDone(beanDefinitions);
    }
}
