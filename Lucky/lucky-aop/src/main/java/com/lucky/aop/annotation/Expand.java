package com.lucky.aop.annotation;

import java.lang.annotation.*;

/**
 * 扩展注解
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/28 上午4:17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Expand {
}
