package lol.cicco.ioc.core.module.conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface ConditionalOnMissBeanType {

    /**
     * 指定BeanType
     */
    Class<?>[] value() default {};

}
