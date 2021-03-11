package com.lucky.aop.annotation;

import com.lucky.aop.enums.Location;

import java.lang.annotation.*;

/**
 * 声明一个环绕增强
 * @author fk-7075
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Expand(Location.AROUND)
public @interface Around {

	/**
	 * 切面表达式
	 * @see Pointcut#value()
	 * @return 切面表达式
	 */
	String value();

	/**
	 * 优先级，优先级高的增强将会被优先执行
	 * @return
	 */
	double priority() default 5;
	
}
