package lol.cicco.ioc.core;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.core.exception.BeanInitializeException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Slf4j
public class Initialize {

    private BeanContainer container;

    public Initialize() {
        this.container = new BeanContainer();
    }

    public Initialize scanBasePackages(String... packages) {
        for (String pkg : new HashSet<>(Arrays.asList(packages))) {
            scan(pkg);
        }
        return this;
    }

    public Initialize scanBasePackageClasses(Class<?>... classes) {
        String[] packages = new String[classes.length];
        for (int i = 0; i < classes.length; i++) {
            packages[i] = classes[i].getPackageName();
        }
        return scanBasePackages(packages);
    }

    public void done() {
        // 初始化完成
        IOC.initializeDone(container);
    }

    private void scan(String pkg) {
        ClassPathScanner scanner = new ClassPathScanner();
        List<BeanDefinition> definitions = scanner.doScan(pkg, Initialize.class.getClassLoader());

        Queue<BeanDefinition> injectQueue = new LinkedList<>();
        for(BeanDefinition definition : definitions) {
            Class<?> type = definition.getBeanType();

            // 被重复扫描
            if(container.contains(type)) {
                log.debug("[{}] 已经被注册至IOC.. 跳过..", type.toString());
                continue;
            }

            try {
                Constructor<?> defConstructor = type.getConstructor();
                Object obj = defConstructor.newInstance();
                container.registerBean(definition, obj);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new BeanInitializeException("["+type.toString()+"] 没有默认构造函数....", e);
            }
            log.debug("[{}] 注册至IOC...", type.toString());
            injectQueue.add(definition);
        }

        while(!injectQueue.isEmpty()) {
            BeanDefinition definition = injectQueue.poll();
            Object bean = container.getBeanByType(definition.getBeanType());

            log.debug("执行注入, 当前目标:{}", definition.getBeanType().toString());

            Field[] fields = definition.getBeanType().getDeclaredFields();
            for(Field field : fields) {
                if(field.getAnnotationsByType(Inject.class) == null) {
                    continue;
                }
                if(!field.canAccess(bean)) {
                    field.setAccessible(true);
                }

                Object injectObj = container.getBeanByType(field.getType());
                try {
                    field.set(bean, injectObj);
                } catch (IllegalAccessException ignore) {
                    // nop..
                }
            }
        }
    }
}
