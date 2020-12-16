package com.lucky.jacklamb.annotation.mapper;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResultType {
	String value() default "";

}
