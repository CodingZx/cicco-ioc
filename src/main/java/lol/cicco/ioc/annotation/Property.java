package lol.cicco.ioc.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Property {

    /**
     * 绑定属性前置名称, 例如 <span>lol.property</span>
     */
    String prefix() default "";

    /**
     * 是否指定所有注入属性可以运行时刷新
     */
    boolean refresh() default false;
}
