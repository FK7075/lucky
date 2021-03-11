package com.lucky.aop.annotation;

import com.lucky.aop.enums.Location;

import java.lang.annotation.*;

/**
 * 真实方法正常执行后执行
 * @author fk-7075
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Expand(Location.AFTER_RETURNING)
public @interface AfterReturning {

	/**
	 * 切面表达式
	 * @see Pointcut#value()
	 * @return 切面表达式
	 */
	String value();

	/**
	 *
	 * @return the name of the argument in the advice signature to bind the returned value to
	 */
	String returning() default "";

	/**
	 * 优先级，优先级高的增强将会被优先执行
	 * @return
	 */
	double priority() default 5;
	
}
