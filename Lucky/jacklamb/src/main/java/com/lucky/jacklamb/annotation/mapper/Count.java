package com.lucky.jacklamb.annotation.mapper;

import java.lang.annotation.*;

/**
 * 定义一个COUNT查询
 * @author fk-7075
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Count {
	String value() default "";

}
