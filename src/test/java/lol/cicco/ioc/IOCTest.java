package lol.cicco.ioc;

import lol.cicco.ioc.aop.LogInterceptor;
import lol.cicco.ioc.aop.SystemClock;
import lol.cicco.ioc.aop.SystemLog;
import lol.cicco.ioc.aop.TimeInterceptor;
import lol.cicco.ioc.bean.BinderBean;
import lol.cicco.ioc.bean.TestBean;
import lol.cicco.ioc.bean.TestBean2;
import lol.cicco.ioc.binder.TestEnum;
import lol.cicco.ioc.core.IOC;
import lol.cicco.ioc.core.LocalDateTimeBinderHandler;
import lol.cicco.ioc.core.binder.EnumPropertyHandler;
import org.junit.Assert;
import org.junit.Test;

public class IOCTest {

    static {
        IOC.initialize()
                .scanBasePackages("lol.cicco.bean")
                .scanBasePackageClasses(TestBean.class)
                .loadProperties("app.prop")
                .loadProperties("prop/app1.prop")
                .loadProperties("prop/app2.prop")
                .registerPropertyHandler(new LocalDateTimeBinderHandler())
                .registerPropertyHandler(new EnumPropertyHandler<>(TestEnum.class))

                .registerInterceptor(SystemClock.class, new TimeInterceptor())
                .registerInterceptor(SystemLog.class, new LogInterceptor())
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
    }

    @Test
    public void inject(){
        TestBean2 testBean2 = IOC.getBeanByType(TestBean2.class);
        testBean2.print();

        TestBean2 testBean2ByName = IOC.getBeanByName("testBean2");
        Assert.assertEquals(testBean2, testBean2ByName);


        Assert.assertEquals("a", IOC.getProperty("a.text", "b"));
        testBean2.printShowText();

        Assert.assertTrue(testBean2.noRegisterClassIsNull());

        TestBean testBean = IOC.getBeanByType(TestBean.class);
        testBean.test();
    }

    @Test
    public void binder(){
        BinderBean binderBean = IOC.getBeanByType(BinderBean.class);

        binderBean.print();

        TestEnum testEnum = IOC.getProperty("test.enum", TestEnum.class);
        Assert.assertEquals(testEnum, TestEnum.THREE);

        System.out.println(binderBean.getClass().getName());
    }


}