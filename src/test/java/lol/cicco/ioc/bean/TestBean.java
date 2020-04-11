package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;

@Registration(name = "testBean1111")
public class TestBean {
    @Inject(byName = "test_impl_1")
    private TestInterface testInterface;

    public void test() {
        testInterface.printTest();
    }

    public void println(){
        System.out.println("啦啦啦啦啦 println");
    }
}
