package com.lucky.jacklamb.annotation.mapper;

import java.lang.annotation.*;

/**
 * Mapper接口的参数描述
 * @author fk-7075
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

	String value();
}
