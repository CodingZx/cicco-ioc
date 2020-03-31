package lol.cicco.ioc.core.scanner;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface BeanScanner {

    /**
     * 扫描URL下所有对应后缀文件
     */
    List<ResourceMeta> doScan(URL url, String suffix) throws IOException;

}
