package lol.cicco.ioc.annotation.condition;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE,METHOD})
public @interface ConditionalOnMissingBeanType {

    /**
     * 若指定的BeanType未注册, 则使用当前定义BeanType进行检测
     */
    Class<?>[] value() default {};
}
