package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.aop.SystemClock;

@Registration(name = "testBean1111")
public class TestBean {
    @Inject(byName = "test_impl_1")
    private TestInterface testInterface;

    @Inject
    private TestBean testBean;

    @Binder(value = "test-fk-value", defaultValue = "10", refresh = true)
    private long value;

    public void test() {
        testInterface.printTest();
    }

    public void println() {
        System.out.println("啦啦啦啦啦 println");
    }

    @SystemClock
    private void privatePrint() {
        System.out.println("privatePrint....");
    }

    public void testPrivatePrint() {
        testBean.privatePrint();
    }
}
