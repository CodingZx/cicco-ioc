package lol.cicco.ioc.core.module.register;

import lombok.Data;

import java.lang.annotation.Annotation;

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
     * 文件路径
     */
    private String filePath;
}
