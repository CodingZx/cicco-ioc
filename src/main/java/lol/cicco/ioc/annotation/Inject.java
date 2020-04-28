package lol.cicco.ioc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
public @interface Inject {

    /**
     * 注入项是否为必须
     */
    boolean required() default true;

    /**
     * 根据名称注入, ""则使用Type注入
     */
    String byName() default "";
}
