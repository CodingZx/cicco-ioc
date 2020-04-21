package lol.cicco.ioc.core.module.register;

import lombok.Data;

import java.lang.reflect.Constructor;

@Data
class AnalyzeBeanDefine {
    // bean类型
    private Class<?> beanType;
    // Bean名称
    private String beanName;
    // 构造函数
    private Constructor<?> beanConstructor;

    AnalyzeBeanDefine(Class<?> beanType,String beanName, Constructor<?> beanConstructor) {
        this.beanConstructor = beanConstructor;
        this.beanName = beanName;
        this.beanType = beanType;
    }

}
