package lol.cicco.ioc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Inject {

    /**
     * 依赖是否必须
     */
    boolean required() default true;

    /**
     * 注入Bean名称
     */
    String byName() default "";
}
