package com.lucky.aop.annotation;

import java.lang.annotation.*;

/**
 * AOP缓存，用于方法上，将方法的返回值以指定的key加入缓存
 * @author fk-7075
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cacheable {
	
	/**
	 * 缓存容器的名字
	 * @return 缓存名字
	 */
	String value();
	
	/**
	 * 缓存对象在缓存容器中的key
	 * @return Key
	 */
	String key();
	
	/**
	 * 条件，满足此条件则执行缓存
	 * @return condition
	 */
	String condition() default "";
}
