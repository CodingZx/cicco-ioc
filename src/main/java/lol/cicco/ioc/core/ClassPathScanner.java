package lol.cicco.ioc.core;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.exception.BeanDefinitionStoreException;
import lol.cicco.ioc.core.scanner.BeanScanner;
import lol.cicco.ioc.core.scanner.FileClassScanner;
import lol.cicco.ioc.core.scanner.JarClassScanner;
import lol.cicco.ioc.core.scanner.ResourceMeta;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class ClassPathScanner {

    public List<BeanDefinition> doScan(String path, ClassLoader classLoader) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        path = path.replace(".", "/");
        List<ResourceMeta> allClasses = new LinkedList<>();
        try {
            Enumeration<URL> urls = classLoader.getResources(path);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();

                BeanScanner scanner = null;
                switch (url.getProtocol()) {
                    case CiccoConstants.URL_PROTOCOL_JAR:
                        scanner = JarClassScanner.getInstance();
                        break;
                    case CiccoConstants.URL_PROTOCOL_FILE:
                        scanner = FileClassScanner.getInstance();
                        break;
                }

                if(scanner != null) {
                    allClasses.addAll(scanner.doScan(url, CiccoConstants.CLASS_FILE_SUFFIX));
                } else {
                    log.warn("未找到对应BeanScanner, urlProtocol为:{}", url.getProtocol());
                }
            }
        } catch (IOException e) {
            throw new BeanDefinitionStoreException("扫描Class时出现异常..", e);
        }

        List<BeanDefinition> beanDefinitions = new LinkedList<>();
        for (ResourceMeta meta : allClasses) {
            try {
                String className = meta.getFileName();
                Class<?> cls = Class.forName(className.substring(0, className.lastIndexOf(CiccoConstants.CLASS_FILE_SUFFIX)), false, classLoader);
                if (!isRegistrationBean(cls)) {
                    // 未托管至IOC
                    continue;
                }
                if (cls.isInterface()) {
                    // 暂时无法处理接口
                    continue;
                }

                BeanDefinition definition = new BeanDefinition();
                definition.setBeanType(cls);

                beanDefinitions.add(definition);
            } catch (ClassNotFoundException e) {
                log.warn(e.getMessage(), e);
            }
        }
        return beanDefinitions;
    }

    private boolean isRegistrationBean(Class<?> cls) {
        return cls.getAnnotation(Registration.class) != null;
    }
}
