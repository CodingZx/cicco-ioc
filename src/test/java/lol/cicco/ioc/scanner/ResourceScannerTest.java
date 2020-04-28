package lol.cicco.ioc.scanner;

import lol.cicco.ioc.core.scanner.ResourceScanner;
import lombok.SneakyThrows;
import org.junit.Test;

public class ResourceScannerTest {

    @Test
    @SneakyThrows
    public void test() {
        ResourceScanner scanner = new ResourceScanner();
        var scanResources = scanner.doScan("lol.cicco", ResourceScannerTest.class.getClassLoader());
        scanResources.forEach(System.out::println);

        var urls = ResourceScannerTest.class.getClassLoader().getResources("lol/cicco/ioc/core/");
        while (urls.hasMoreElements()) {
            System.out.println(urls.nextElement());
        }
    }
}
