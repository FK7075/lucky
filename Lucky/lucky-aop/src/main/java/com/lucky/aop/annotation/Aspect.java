package com.lucky.aop.annotation;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.annotation.Plugin;

import java.lang.annotation.*;

/**
 * 声明一个代理对象, 该对象将会被当作一种增强
 * @author fk-7075
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Plugin
@Component(type = "aop")
public @interface Aspect {
	
	/**
	 * 设置一个组件ID
	 * @return ID
	 */
	String value() default "";
}