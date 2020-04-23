package lol.cicco.ioc.bean.method;

import lol.cicco.ioc.annotation.Registration;

@Registration
public class MethodDefineClass {

    @Registration
    public TestBeanInterface createBean() {
        return new TestBeanInterface() {
            @Override
            public void run() {
                System.out.println("run...");
            }

            @Override
            public void aabbc() {
                System.out.println("aabbc");
            }
        };
    }
}
