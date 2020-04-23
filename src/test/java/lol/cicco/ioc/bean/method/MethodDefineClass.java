package lol.cicco.ioc.bean.method;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.aop.SystemClock;

@Registration
public class MethodDefineClass {

    @Registration
    public TestBeanInterface createBean() {
        return new TestBeanInterface() {
            @Override
            @SystemClock
            public void run() {
                System.out.println("run...");
            }

            @Override
            @SystemClock
            public void aabbc() {
                System.out.println("aabbc");
            }
        };
    }
}
