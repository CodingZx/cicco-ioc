package lol.cicco.ioc;

import lol.cicco.ioc.bean.TestBean;
import lol.cicco.ioc.bean.TestBean2;
import lol.cicco.ioc.core.IOC;
import org.junit.Test;

public class IOCTest {

    static {
        IOC.initialize().scanBasePackages("lol.cicco").scanBasePackageClasses(TestBean.class).done();
    }

    @Test
    public void inject(){
        TestBean2 testBean2 = IOC.getBeanByType(TestBean2.class);
        testBean2.print();
    }
}