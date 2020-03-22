package lol.cicco.ioc.core.scanner;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface BeanScanner {

    /**
     * 扫描URL下所有注册至IOC的Bean
     */
    List<ClassMeta> doScan(URL url) throws IOException;

}
