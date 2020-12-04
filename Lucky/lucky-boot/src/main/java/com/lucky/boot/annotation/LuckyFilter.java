package com.lucky.boot.annotation;

import com.lucky.framework.annotation.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component(type = "filter")
public @interface LuckyFilter {
	
	String[] value();

}
