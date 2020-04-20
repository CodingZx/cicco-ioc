package lol.cicco.ioc.mybatis;

import lol.cicco.ioc.core.scanner.ResourceMeta;
import lol.cicco.ioc.core.scanner.ResourceScanner;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
class XmlResourceScanner extends ResourceScanner {

    public Set<ResourceMeta> scanXml(String path, ClassLoader classLoader) {
        Set<ResourceMeta> resourceMetas = super.doScan(path, classLoader);

        // 分析Class, 是否为需要注册至IOC
        Set<ResourceMeta> classDefinitions = new LinkedHashSet<>();
        for (ResourceMeta meta : resourceMetas) {
            if (!meta.getFileName().endsWith(".xml")) {
                // 过滤非class文件
                continue;
            }
            classDefinitions.add(meta);
        }
        return classDefinitions;
    }

}
