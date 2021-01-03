package com.lucky.web.authority.annotation;

import com.lucky.web.authority.Logical;

import java.lang.annotation.*;

/**
 * 必须拥有该权限的用户便可以访问
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/4 上午12:27
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MustPermissions {

    String[] value();

    Logical logical() default Logical.AND;
}
