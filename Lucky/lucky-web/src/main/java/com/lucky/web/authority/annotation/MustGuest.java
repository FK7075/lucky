package com.lucky.web.authority.annotation;

import java.lang.annotation.*;

/**
 * 游客省份才可以访问
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/4 上午12:27
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MustGuest {
}
