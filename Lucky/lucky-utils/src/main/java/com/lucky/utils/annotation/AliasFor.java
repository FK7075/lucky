package com.lucky.utils.annotation;

import java.lang.annotation.*;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/22 上午2:18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AliasFor {

    @AliasFor("attribute")
    String value() default "";

    @AliasFor("value")
    String attribute() default "";

    Class<? extends Annotation> annotation() default Annotation.class;
}
