package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Binder;
import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;

@Registration
public class TestBean2 {
    @Inject
    private TestBean testBean;

    @Binder("a.text")
    private String showAText;

    public void print(){
        System.out.println("testBean is "+testBean);
    }

    public void printShowText(){
        System.out.println(showAText);
    }
}
