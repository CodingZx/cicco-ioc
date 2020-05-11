package lol.cicco.ioc;

import lol.cicco.ioc.aop.TimeAnnotationInterceptor;
import lol.cicco.ioc.bean.*;
import lol.cicco.ioc.binder.TestEnum;
import lol.cicco.ioc.core.BeanNotFountException;
import lol.cicco.ioc.core.IOC;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lol.cicco.ioc.mybatis.MybatisModule;
import lol.cicco.ioc.service.TestBeanService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class IOCTest {

    static {
        IOC.initialize()
                .scanBasePackages("lol.cicco.ioc.bean")
                .scanBasePackages("lol.cicco.ioc.aop")
                .scanBasePackages("lol.cicco.ioc.binder")
                .scanBasePackages("lol.cicco.ioc.mybatis")
                .scanBasePackages("lol.cicco.ioc.service")
                .loadProperties("app.prop")
                .loadProperties("prop/app1.prop")
                .loadProperties("prop/app2.prop")
                .loadProperties("mybatis.prop")
                .loadYaml("test.yml")
                .registerModule(new MybatisModule())
                .done()
        ;
    }
    @Test
    public void testAop() {
        System.out.println("-------");
        IOC.setProperty("test.enum", "THREE");
        var time = new TimeAnnotationInterceptor();
        time.before(null);
        TestBean2 testBean2 = new TestBean2();
        testBean2.print();
        time.after(null);

        System.out.println("-------");

        TestBean2 testBean11 = IOC.getBeanByType(TestBean2.class);
        testBean11.print();
        System.out.println("-------");

        TestBean2 testBean12 = IOC.getBeanByType(TestBean2.class);
        testBean12.print();
        System.out.println("-------");

        TestBean2 testBean13 = IOC.getBeanByType(TestBean2.class);
        testBean13.print();

        TestBean testBean = IOC.getBeanByType(TestBean.class);
        testBean.testPrivatePrint();
    }

    @Test
    public void inject() {
        TestBean2 testBean2 = IOC.getBeanByType(TestBean2.class);
        testBean2.test(100);

        TestBean2 testBean21 = IOC.getBeanByType(TestBean2.class);
        testBean21.test(100);

        TestBean2 testBean2ByName = IOC.getBeanByName("testBean2");
        Assert.assertEquals(testBean2, testBean2ByName);


        Assert.assertEquals("a", IOC.getProperty("a.text", "b"));
        testBean2.printShowText();

        Assert.assertTrue(testBean2.noRegisterClassIsNull());

        TestBean testBean = IOC.getBeanByType(TestBean.class);
        testBean.test();
    }

    @Test
    public void binder() {
        IOC.removeProperty("test.enum");
        BinderBean binderBean = IOC.getBeanByType(BinderBean.class);

        binderBean.print();

        TestEnum testEnum = IOC.getProperty("test.enum", TestEnum.class);
//        Assert.assertEquals(testEnum, TestEnum.THREE);
        Assert.assertNull(testEnum);
        Assert.assertNull(binderBean.getTestEnum());

        System.out.println(binderBean.getClass().getName());
        BinderBean binderBean2 = IOC.getBeanByType(BinderBean.class);
        BinderBean binderBean3 = IOC.getBeanByType(BinderBean.class);

        Assert.assertEquals(10, binderBean.getValue());

        IOC.setProperty("test-fk-value", "20");
        binderBean = IOC.getBeanByType(BinderBean.class);
        Assert.assertEquals(20, binderBean.getValue());
        Assert.assertEquals(20, binderBean2.getValue());
        Assert.assertEquals(20, binderBean3.getValue());

        Assert.assertEquals("1234", IOC.getProperty("aaa.bbb.ccc"));
    }

    @AfterClass
    public static void afterClass() {

        System.gc();
    }

    @Test
    public void mybatis() {
        TestBeanService testBeanService = IOC.getBeanByType(TestBeanService.class);
        testBeanService.save(false);

        testBeanService.save(false);

        testBeanService.all().forEach(a -> testBeanService.delete(a.getId()));
    }

    @Test
    public void testInjectByConstructor() {
        TestBeanByConstructor testBeanByConstructor = IOC.getBeanByType(TestBeanByConstructor.class);
        testBeanByConstructor.test();
    }

}