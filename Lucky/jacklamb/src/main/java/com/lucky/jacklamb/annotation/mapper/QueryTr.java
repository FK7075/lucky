package com.lucky.jacklamb.annotation.mapper;

import java.lang.annotation.*;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/8/18 14:42
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryTr {
    String value();
}
