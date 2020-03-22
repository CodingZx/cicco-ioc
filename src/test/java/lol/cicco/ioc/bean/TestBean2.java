package lol.cicco.ioc.bean;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;

@Registration
public class TestBean2 {
    @Inject
    private TestBean testBean;

    public void print(){
        System.out.println("testBean is "+testBean);
    }
}
