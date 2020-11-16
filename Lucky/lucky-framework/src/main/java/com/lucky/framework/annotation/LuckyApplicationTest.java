package com.lucky.framework.annotation;

import java.lang.annotation.*;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/16 15:41
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LuckyApplicationTest {

    Class<?> rootClass() default Void.class;
}
