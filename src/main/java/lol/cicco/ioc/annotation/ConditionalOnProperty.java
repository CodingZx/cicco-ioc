package lol.cicco.ioc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface ConditionalOnProperty {

    /**
     * 指定属性名
     */
    String[] name() default {};

    /**
     * 判断属性值是否与指定的值相同, 如果指定的值为空, 则判断属性是否存在
     */
    String havingValue() default "";
}
