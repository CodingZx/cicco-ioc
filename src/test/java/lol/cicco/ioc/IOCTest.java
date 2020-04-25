package lol.cicco.ioc;

import lol.cicco.ioc.aop.TimeAnnotationInterceptor;
import lol.cicco.ioc.bean.BinderBean;
import lol.cicco.ioc.bean.TestBean;
import lol.cicco.ioc.bean.TestBean2;
import lol.cicco.ioc.bean.TestBeanByConstructor;
import lol.cicco.ioc.binder.TestEnum;
import lol.cicco.ioc.core.IOC;
import lol.cicco.ioc.core.LocalDateTimeBinderHandler;
import lol.cicco.ioc.core.module.property.EnumPropertyHandler;
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
    volatile boolean flag = false;

    @Test
    public void binder() {
        BinderBean binderBean = IOC.getBeanByType(BinderBean.class);

        binderBean.print();

        TestEnum testEnum = IOC.getProperty("test.enum", TestEnum.class);
        Assert.assertEquals(testEnum, TestEnum.THREE);

        System.out.println(binderBean.getClass().getName());
        BinderBean binderBean2 = IOC.getBeanByType(BinderBean.class);
        BinderBean binderBean3 = IOC.getBeanByType(BinderBean.class);

        Assert.assertEquals(10, binderBean.getValue());
//        binderBean = IOC.getBeanByType(BinderBean.class);

        new Thread(() -> {
            while (!flag) {
                BinderBean test111 = IOC.getBeanByType(BinderBean.class);
                try {
                    int size = 1024 * 1024 * 1024;
                    byte[] bytes = new byte[size];
                    byte[] bytes2 = new byte[size];
                    byte[] bytes3 = new byte[size];
                    byte[] bytes4 = new byte[size];
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                for (int v = 0; v < 6; v++) {
                    int size = 1024 * 1024 * 1024;
                    byte[] bytes = new byte[size];
                    byte[] bytes2 = new byte[size];
                    byte[] bytes3 = new byte[size];
                    byte[] bytes4 = new byte[size];
                    System.out.println("创建[" + (size) + "]数组....");
                }

                TestBean bb = IOC.getBeanByType(TestBean.class);
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                IOC.setProperty("test-fk-value", String.valueOf(System.currentTimeMillis()));


                BinderBean test111 = IOC.getBeanByType(BinderBean.class);

                System.gc();
            }

            flag = true;

        }).start();

        while (true) {
            if (flag) {
                break;
            }
        }

        IOC.setProperty("test-fk-value", "20");
        binderBean = IOC.getBeanByType(BinderBean.class);
        Assert.assertEquals(20, binderBean.getValue());
        Assert.assertEquals(20, binderBean2.getValue());
        Assert.assertEquals(20, binderBean3.getValue());
    }
    @AfterClass
    public static void afterClass(){

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