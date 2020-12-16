package com.lucky.jacklamb.annotation.mapper;

import java.lang.annotation.*;

/**
 * 设置自增主键
 * @author fk-7075
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoId {
	String value() default "";
}
