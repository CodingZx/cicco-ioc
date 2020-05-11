package lol.cicco.ioc.core.module.initialize;

public interface InitializingBean {

    void afterPropertySet() throws Exception;

}
