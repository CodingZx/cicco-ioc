package lol.cicco.ioc.core.scanner;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface ProtocolResourceScanner {

    /**
     * 扫描URL下所有文件
     */
    List<ResourceMeta> doScan(URL url) throws IOException;

}
