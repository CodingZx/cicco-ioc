package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.binder.TestEnum;

@Registration(name = "testBean2")
public class TestBean2 {
    @Inject(byName = "testBean1111")
    private TestBean testBean;

    @Binder("a.text")
    private String showAText;

    @Binder("b.num")
    private int b;

    @Binder("test.enum")
    private TestEnum testEnum;

    @Inject(required = false, byName = "noReg2")
    private NoRegisterClass cls;

    public void print(){
        System.out.println("testBean is "+testBean);
    }

    public void printShowText(){
        System.out.println(showAText);
        System.out.println("b:"+b);
        System.out.println("enum : "+ testEnum);
    }

    public boolean noRegisterClassIsNull(){
        return cls == null;
    }
}
