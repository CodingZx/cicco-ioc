package lol.cicco.ioc.inject;

import lol.cicco.ioc.annotation.InjectConstructor;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.inject.constructor.TestBeanInject;

@Registration
public class TestConstructor {

    @InjectConstructor
    public TestConstructor() {

    }

    @InjectConstructor
    public TestConstructor(TestBeanInject testBeanInject) {

    }

}
