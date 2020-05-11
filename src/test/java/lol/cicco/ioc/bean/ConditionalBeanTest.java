package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Registration;

@Registration
public class ConditionalBeanTest {

    @Registration(name = "methodTestInterface")
    public TestInterface testBeanDefine() {

        return () -> System.out.println("Method define testInterface....");
    }

    @Registration(name = "conditionalBean")
    public ConditionBean conditionBean(){
        return new ConditionBean();
    }

    public static class ConditionBean {

        public void test() {
            System.out.println("conditional bean test method///");
        }
    }
}
