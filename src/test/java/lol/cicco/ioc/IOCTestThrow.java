package lol.cicco.ioc;

import lol.cicco.ioc.core.IOC;
import lol.cicco.ioc.core.module.beans.BeanStoreException;
import org.junit.Test;

public class IOCTestThrow {

    @Test(expected = BeanStoreException.class)
    public void testThrow() {

        // testBean2被注册两次
        IOC.initialize()
                .scanBasePackages("lol.cicco.ioc.bean", "lol.cicco.ioc.bean2")
                .done()
        ;
    }
}
