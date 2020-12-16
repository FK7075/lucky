package com.lucky.jacklamb.annotation.mapper;

import java.lang.annotation.*;

/**
 * 定义一个更新操作的条件参数配合@Update注解使用
 * @author fk-7075
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface X {
	String value() default "";
}
