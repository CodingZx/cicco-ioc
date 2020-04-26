package lol.cicco.ioc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD})
public @interface Binder {
    /**
     * 绑定对应PropertyName
     */
    String value();

    /**
     * 指定默认值
     */
    String defaultValue() default "";

    /**
     * 是否指定注入属性可以运行时刷新, Property注解中refresh为true时失效
     */
    boolean refresh() default false;

    /**
     * 不存在属性值是否注入Null
     */
    boolean noValueToNull() default false;
}
