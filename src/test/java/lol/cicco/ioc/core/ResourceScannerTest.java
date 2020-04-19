package lol.cicco.ioc.core;

import lol.cicco.ioc.core.scanner.ResourceScanner;
import org.junit.Test;

import java.lang.reflect.Constructor;

public class ResourceScannerTest {

    @Test
    public void doScan() {
        ResourceScanner scanner = new ResourceScanner();

        scanner.doScan("org", ResourceScanner.class.getClassLoader());

        for(Constructor<?> constructor : scanner.getClass().getConstructors()) {
            System.out.println(constructor.getParameterCount());
            System.out.println(constructor.canAccess(null));
        }

    }
}