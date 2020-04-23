package lol.cicco.ioc.bean.method;

import lol.cicco.ioc.aop.SystemLog;

public interface TestBeanInterface {

    @SystemLog
    void run();

    @SystemLog
    void aabbc();
}
