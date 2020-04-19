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
}
