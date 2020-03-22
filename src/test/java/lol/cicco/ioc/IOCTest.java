package lol.cicco.ioc;

import lol.cicco.ioc.bean.TestBean2;
import org.junit.Test;

import static org.junit.Assert.*;

public class IOCTest {

    static {
        IOC.initialize("lol.cicco");
    }

    @Test
    public void inject(){
        TestBean2 testBean2 = IOC.getBeanByType(TestBean2.class);
        testBean2.print();
    }
}