package lol.cicco.ioc;

import lol.cicco.ioc.bean.BinderBean;
import lol.cicco.ioc.bean.TestBean;
import lol.cicco.ioc.bean.TestBean2;
import lol.cicco.ioc.binder.TestEnum;
import lol.cicco.ioc.core.IOC;
import lol.cicco.ioc.core.LocalDateTimeBinderHandler;
import lol.cicco.ioc.core.binder.EnumBindHandler;
import org.junit.Assert;
import org.junit.Test;

public class IOCTest {

    static {
        IOC.initialize()
                .scanBasePackages("lol.cicco")
                .scanBasePackageClasses(TestBean.class)
                .loadProperties("app.prop")
                .loadProperties("prop/app1.prop")
                .loadProperties("prop/app2.prop")
                .registerBindHandler(new LocalDateTimeBinderHandler())
                .registerBindHandler(new EnumBindHandler<>(TestEnum.class))
                .done()
        ;
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
    }

    @Test
    public void binder(){
        BinderBean binderBean = IOC.getBeanByType(BinderBean.class);

        binderBean.print();
    }

}