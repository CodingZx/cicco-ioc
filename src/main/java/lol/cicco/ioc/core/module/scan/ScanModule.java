package lol.cicco.ioc.core.module.scan;

import lol.cicco.ioc.core.CiccoContext;
import lol.cicco.ioc.core.CiccoModule;
import lol.cicco.ioc.core.module.beans.BeanModule;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class ScanModule implements CiccoModule<ResourceScanner> {
    public static final String SCAN_MODULE_NAME = "_scanModule";

    private ResourceScanner scanner;

    @Override
    public void initModule(CiccoContext context) {
        scanner = new ResourceScannerImpl();
        log.debug("init scan module....");
    }

    @Override
    public String getModuleName() {
        return SCAN_MODULE_NAME;
    }

    @Override
    public ResourceScanner getModuleProcessor() {
        return scanner;
    }

    @Override
    public List<String> dependModule() {
        return null;
    }

    @Override
    public List<String> afterModule() {
        return Collections.singletonList(BeanModule.BEAN_MODULE_NAME);
    }
}
