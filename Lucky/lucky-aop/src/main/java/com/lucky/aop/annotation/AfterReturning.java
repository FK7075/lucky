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
	 * 设置增强方法的唯一标记(默认值：方法名)
	 * @return
	 */
	String value() default "";

	/**
	 * 切面表达式
	 * P:{包检验表达式}
	 * C:{N[类名检验表达式],I[IOC_ID校验表达式],T[IOC_TYPE校验表达式],A[是否被注解]}
	 * M:{N[方法名校验表达式],A[是否被注解],AC[访问修饰符],O[要增强的继承自Object对象的方法]}
	 * P:{*}C:{N[HelloController,MyService]}M:{AC[*],N[show,query(int,String)]}
	 * @return
	 */
	String expres() default "";

	/**
	 * 优先级，优先级高的增强将会被优先执行
	 * @return
	 */
	double priority() default 5;
	
}
