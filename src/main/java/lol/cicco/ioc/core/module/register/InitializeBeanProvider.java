package lol.cicco.ioc.core.module.register;

import lol.cicco.ioc.core.module.beans.BeanProvider;

interface InitializeBeanProvider {

    void initialize() throws Exception;

    BeanProvider getBeanProvider();
}
