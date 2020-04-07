package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Registration;

@Registration(name = "test_impl_1")
public class TestInterfaceImpl implements TestInterface {
    @Override
    public void printTest() {
        System.out.println("testImpl1");
    }
}
