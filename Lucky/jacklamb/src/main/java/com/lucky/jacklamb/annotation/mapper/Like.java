package com.lucky.jacklamb.annotation.mapper;

import java.lang.annotation.*;

/**
 * 定义一个模糊查询参数，配合@Query注解使用
 * @author fk-7075
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Like {
	String value() default "";
}
