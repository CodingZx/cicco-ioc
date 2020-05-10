package lol.cicco.ioc.bean;

import lol.cicco.ioc.core.module.conditional.ConditionalOnMissBeanType;
import lol.cicco.ioc.core.module.conditional.ConditionalOnProperty;
import lol.cicco.ioc.annotation.Registration;

@Registration
public class ConditionalBeanTest {

    @Registration(name = "methodTestInterface")
    @ConditionalOnMissBeanType(TestInterface.class)
    public TestInterface testBeanDefine() {

        return () -> System.out.println("Method define testInterface....");
    }

    @Registration(name = "conditionalBean")
    @ConditionalOnMissBeanType(ConditionBean.class)
    @ConditionalOnProperty(name = "conditional.prop", havingValue = "test")
    public ConditionBean conditionBean(){
        return new ConditionBean();
    }

    public static class ConditionBean {

        public void test() {
            System.out.println("conditional bean test method///");
        }
    }
}
