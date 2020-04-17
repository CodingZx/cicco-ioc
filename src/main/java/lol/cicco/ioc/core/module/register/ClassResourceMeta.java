package lol.cicco.ioc.core.module.register;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.Set;

@Data
public class ClassResourceMeta {

    /**
     * 自身类型
     */
    private Class<?> selfType;

    /**
     * 注解
     */
    private Annotation[] annotations;

    /**
     * 是否为接口类型
     */
    private boolean interfaceType;

    /**
     * 是否为抽象类
     */
    private boolean abstractType;
}
