package lol.cicco.ioc;

import lol.cicco.ioc.core.IOC;
import lol.cicco.ioc.core.exception.BeanDefinitionStoreException;
import org.junit.Test;

public class IOCTestThrow {

    @Test(expected = BeanDefinitionStoreException.class)
    public void testThrow() {

        // testBean2被注册两次
        IOC.initialize()
                .scanBasePackages("lol.cicco.ioc.bean", "lol.cicco.ioc.bean2")
                .done()
        ;
    }
}
