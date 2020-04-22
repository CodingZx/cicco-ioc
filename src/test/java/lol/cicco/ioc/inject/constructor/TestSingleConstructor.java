package lol.cicco.ioc.inject.constructor;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.InjectConstructor;
import lol.cicco.ioc.annotation.Registration;
import lombok.Getter;

@Registration
public class TestSingleConstructor {

    @Getter
    private TestInterface inject;

    @InjectConstructor
    public TestSingleConstructor(@Inject(required = false) TestInterface inject) {
        this.inject = inject;
    }

    public TestSingleConstructor(TestBeanInject inject, int a) {
        System.out.println(a);
    }
}
