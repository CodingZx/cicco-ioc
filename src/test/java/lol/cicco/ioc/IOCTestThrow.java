package lol.cicco.ioc;

import lol.cicco.ioc.bean2.TestBeanByConstructor;
import lol.cicco.ioc.bean3.Bean1;
import lol.cicco.ioc.bean3.NoRegister;
import lol.cicco.ioc.core.IOC;
import lol.cicco.ioc.core.module.register.RegisterException;
import org.junit.Assert;
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


    @Test(expected = RegisterException.class)
    public void injectConstructorThrow() {
        IOC.initialize()
                .scanBasePackages("lol.cicco.ioc.bean2")
                .done()
        ;

        IOC.getBeanByType(TestBeanByConstructor.class).println();
    }

    @Test
    public void noRegister(){
        IOC.initialize()
                .scanBasePackages("lol.cicco.ioc.bean3")
                .done()
        ;

        NoRegister noRegister = IOC.getBeanByType(Bean1.class).getNoRegister();
        Assert.assertNull(noRegister);
    }
}
