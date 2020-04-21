package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Registration;

@Registration
public class TestBeanByConstructor {

    private final TestBean testBean;

    public TestBeanByConstructor(TestBean testBean) {
        System.out.println("TestBeanByConstructor init...." + testBean);
        this.testBean = testBean;
    }

    public void test() {
        testBean.test();
    }

}
