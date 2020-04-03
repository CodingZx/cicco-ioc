package lol.cicco.ioc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Registration {

    /**
     * 定义BeanName, 若为"" 则采用ClassName
     */
    String name() default "";
}
