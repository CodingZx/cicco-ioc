package lol.cicco.ioc.core.module.scan;

import java.util.Set;


public interface ResourceScanner {
    Set<ResourceMeta> doScan(String path, ClassLoader classLoader);
}
