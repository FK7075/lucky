package com.lucky.jacklamb.boot.annotation;

import com.lucky.framework.annotation.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component(type = "listener")
public @interface LuckyListener {
	
	String value() default "";
	
}
