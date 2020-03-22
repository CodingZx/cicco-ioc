package lol.cicco.ioc.core;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

public class ClassPathScannerTest {

    @Test
    public void doScan() {
        ClassPathScanner scanner = new ClassPathScanner();

        scanner.doScan("org", ClassPathScanner.class.getClassLoader());


        for(Constructor<?> constructor : scanner.getClass().getConstructors()) {
            System.out.println(constructor.getParameterCount());
            System.out.println(constructor.canAccess(null));
        }

    }
}