package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.core.CiccoConstants;
import lol.cicco.ioc.core.module.scan.ResourceMeta;
import lol.cicco.ioc.core.module.scan.ResourceScanner;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
class ClassResourceScanner {

    private final ResourceScanner scanner;

    public ClassResourceScanner(ResourceScanner scanner) {
        this.scanner = scanner;
    }

    /**
     * 扫描指定路径下Class文件信息
     */
    public Set<ClassResourceMeta> scanClassMeta(String path, ClassLoader classLoader) {
        Set<ResourceMeta> resourceMetas = scanner.doScan(path, classLoader);

        // 分析Class, 是否为需要注册至IOC
        Set<ClassResourceMeta> classDefinitions = new LinkedHashSet<>();
        for (ResourceMeta meta : resourceMetas) {
            if (!meta.getFileName().endsWith(CiccoConstants.CLASS_FILE_SUFFIX)) {
                // 过滤非class文件
                continue;
            }
            try {
                String className = meta.getFileName();
                Class<?> cls = Class.forName(className.substring(0, className.lastIndexOf(CiccoConstants.CLASS_FILE_SUFFIX)), false, classLoader);

                ClassResourceMeta definition = new ClassResourceMeta();
                definition.setSelfType(cls);
                definition.setAnnotations(cls.getDeclaredAnnotations());
                definition.setFilePath(meta.getUri().getPath());

                classDefinitions.add(definition);
            } catch (ClassNotFoundException e) {
                log.warn(e.getMessage(), e);
            }
        }
        return classDefinitions;
    }

}
