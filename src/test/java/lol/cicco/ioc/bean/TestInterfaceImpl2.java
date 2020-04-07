package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Registration;

@Registration(name = "test_impl_2")
public class TestInterfaceImpl2 implements TestInterface {
    @Override
    public void printTest() {
        System.out.println("testImpl2");
    }
}
