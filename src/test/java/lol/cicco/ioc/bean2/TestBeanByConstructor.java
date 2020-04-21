package lol.cicco.ioc.bean2;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;

@Registration(name = "testBean_byConstructor")
public class TestBeanByConstructor {

    private final InjectBean injectBean;

    TestBeanByConstructor(@Inject(byName = "lalala") InjectBean injectBean) { // 找不到对应bean
        this.injectBean = injectBean;
    }

    public void println() {
        injectBean.test();
    }

}
