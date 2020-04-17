package lol.cicco.ioc.core.scanner;

import lol.cicco.ioc.core.CiccoConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class ResourceScanner {

    public Set<ResourceMeta> doScan(String path, ClassLoader classLoader) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        path = path.replace(".", "/");
        Set<ResourceMeta> allResources = new LinkedHashSet<>();
        try {
            Enumeration<URL> urls = classLoader.getResources(path);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();

                ProtocolResourceScanner scanner = null;
                switch (url.getProtocol()) {
                    case CiccoConstants.URL_PROTOCOL_JAR: // 需要从Jar中扫描对应文件
                        scanner = new JarResourceScanner();
                        break;
                    case CiccoConstants.URL_PROTOCOL_FILE: // 需要从本地文件中扫描对应文件
                        scanner = new FileResourceScanner();
                        break;
                }

                if (scanner != null) {
                    allResources.addAll(scanner.doScan(url, CiccoConstants.CLASS_FILE_SUFFIX));
                } else {
                    log.warn("未找到对应BeanScanner, urlProtocol为:{}", url.getProtocol());
                }
            }
        } catch (IOException e) {
            throw new ResourceScanException("扫描Class时出现异常..", e);
        }
        return allResources;
    }

}
