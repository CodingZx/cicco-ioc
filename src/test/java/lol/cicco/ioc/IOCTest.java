package lol.cicco.ioc;

import lol.cicco.ioc.aop.TimeInterceptor;
import lol.cicco.ioc.bean.BinderBean;
import lol.cicco.ioc.bean.TestBean;
import lol.cicco.ioc.bean.TestBean2;
import lol.cicco.ioc.binder.TestEnum;
import lol.cicco.ioc.core.IOC;
import lol.cicco.ioc.core.LocalDateTimeBinderHandler;
import lol.cicco.ioc.core.module.property.EnumPropertyHandler;
import lol.cicco.ioc.mybatis.MybatisModule;
import lol.cicco.ioc.service.TestBeanService;
import org.junit.Assert;
import org.junit.Test;

public class IOCTest {

    static {
        IOC.initialize()
                .scanBasePackages("lol.cicco.ioc.bean")
                .scanBasePackages("lol.cicco.ioc.aop")
                .scanBasePackages("lol.cicco.ioc.mybatis")
                .scanBasePackages("lol.cicco.ioc.service")
                .loadProperties("app.prop")
                .loadProperties("prop/app1.prop")
                .loadProperties("prop/app2.prop")
                .loadProperties("mybatis.prop")
                .registerPropertyHandler(new LocalDateTimeBinderHandler())
                .registerPropertyHandler(new EnumPropertyHandler<>(TestEnum.class))
                .registerModule(new MybatisModule())
                .done()
        ;
    }

    @Test
    public void testAop() throws Throwable {
        System.out.println("-------");

        var time = new TimeInterceptor();
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
        BinderBean binderBean = IOC.getBeanByType(BinderBean.class);

        binderBean.print();

        TestEnum testEnum = IOC.getProperty("test.enum", TestEnum.class);
        Assert.assertEquals(testEnum, TestEnum.THREE);

        System.out.println(binderBean.getClass().getName());
    }

    @Test
    public void mybatis() {
        TestBeanService testBeanService = IOC.getBeanByType(TestBeanService.class);
        testBeanService.save(false);

        testBeanService.save(false);

        testBeanService.all().forEach(a -> testBeanService.delete(a.getId()));
    }

}