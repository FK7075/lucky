package com.lucky.web.annotation;

import java.lang.annotation.*;

/**
 * 定义一个远程资源访问
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CallApi {
	
	/**
	 * 定义一个远程url请求映射
	 * @return
	 */
	String value() default "";

	/**
	 * 调用远程API所需要的参数的参数名
	 * @return
	 */
	String[] paramNames()default {};

	
}
