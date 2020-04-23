package lol.cicco.ioc;

import lol.cicco.ioc.bean.method.TestBeanInterface;
import lol.cicco.ioc.bean2.TestBeanByConstructor;
import lol.cicco.ioc.bean3.Bean1;
import lol.cicco.ioc.bean3.NoRegister;
import lol.cicco.ioc.core.IOC;
import lol.cicco.ioc.core.module.register.RegisterException;
import lol.cicco.ioc.inject.constructor.TestSingleConstructor;
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

    @Test(expected = RegisterException.class)
    public void noRegister(){
        IOC.initialize()
                .scanBasePackages("lol.cicco.ioc.bean3")
                .done()
        ;

        NoRegister noRegister = IOC.getBeanByType(Bean1.class).getNoRegister();
        Assert.assertNull(noRegister);
    }

    @Test
    public void testInjectConstructor() {
        try{
            IOC.initialize()
                    .scanBasePackages("lol.cicco.ioc.inject")
                    .done()
            ;
        } catch (RegisterException e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains("无法使用多个构造函数"));
        }
    }

    @Test
    public void testSingleInjectConstructor() {
        IOC.initialize()
                .scanBasePackages("lol.cicco.ioc.inject.constructor")
                .done()
        ;

        var bean = IOC.getBeanByType(TestSingleConstructor.class);
        Assert.assertNotNull(bean.getInject());
    }



    @Test
    public void testMethodDefine() {
        IOC.initialize()
                .scanBasePackages("lol.cicco.ioc.aop", "lol.cicco.ioc.bean")
                .done()
        ;

        var bean = IOC.getBeanByType(TestBeanInterface.class);

        bean.run();
        bean.aabbc();
    }
}
