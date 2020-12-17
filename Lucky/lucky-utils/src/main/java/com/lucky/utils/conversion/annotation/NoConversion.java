package com.lucky.utils.conversion.annotation;

import java.lang.annotation.*;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/8/19 15:22
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoConversion {
}
