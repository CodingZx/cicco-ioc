package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;

@Registration(name = "testBean2")
public class TestBean2 {
    @Inject
    private TestBean testBean;

    @Binder("a.text")
    private String showAText;

    @Binder("b.num")
    private int b;

    public void print(){
        System.out.println("testBean is "+testBean);
    }

    public void printShowText(){
        System.out.println(showAText);
        System.out.println("b:"+b);
    }
}
