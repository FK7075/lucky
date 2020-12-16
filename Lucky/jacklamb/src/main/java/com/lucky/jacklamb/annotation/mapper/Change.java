package com.lucky.jacklamb.annotation.mapper;

import java.lang.annotation.*;

/**
 * 使用在Mapper接口方法上，开启基于非空检查的动态SQL功能
 * 配合@Select,@Update,@Delete使用
 * @author fk-7075
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Change {
	String value() default "";
}
