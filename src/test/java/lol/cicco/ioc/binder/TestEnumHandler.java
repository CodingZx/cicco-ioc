package lol.cicco.ioc.binder;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.module.property.EnumPropertyHandler;

@Registration
public class TestEnumHandler extends EnumPropertyHandler<TestEnum> {

    public TestEnumHandler() {
        super(TestEnum.class);
    }
}
