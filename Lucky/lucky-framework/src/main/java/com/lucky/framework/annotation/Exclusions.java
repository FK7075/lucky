package com.lucky.framework.annotation;

import java.lang.annotation.*;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/30 0030 15:16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Exclusions {

    Class<?>[] value();

}
