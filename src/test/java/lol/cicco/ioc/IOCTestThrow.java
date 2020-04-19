package lol.cicco.ioc;

import lol.cicco.ioc.core.IOC;
import org.junit.Test;

public class IOCTestThrow {

    @Test(expected = IllegalStateException.class)
    public void testThrow() {

        // testBean2被注册两次
        IOC.initialize()
                .scanBasePackages("lol.cicco.ioc.bean", "lol.cicco.ioc.bean2")
                .done()
        ;
    }
}
