package lol.cicco.ioc.inject.constructor;

import lol.cicco.ioc.annotation.InjectConstructor;
import lol.cicco.ioc.annotation.Registration;
import lombok.Getter;

@Registration
public class TestSingleConstructor {

    @Getter
    private TestBeanInject inject;

    @InjectConstructor
    public TestSingleConstructor(TestBeanInject inject) {
        this.inject = inject;
    }

    public TestSingleConstructor(TestBeanInject inject, int a) {
        System.out.println(a);
    }
}
