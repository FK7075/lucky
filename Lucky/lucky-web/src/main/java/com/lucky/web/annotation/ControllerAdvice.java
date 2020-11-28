package com.lucky.web.annotation;

import com.lucky.web.enums.Rest;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface ControllerAdvice {
	
	String id() default "";

	/**
	 * 视图定位的前缀(eg: /WEB_INF/jsp/),只有在rest=Rest.NO时发挥作用
	 * @return
	 */
	String prefix() default "";

	/**
	 * 视图定位的后缀(eg: .jsp),只有在rest=Rest.NO时发挥作用
	 * @return
	 */
	String suffix() default "";


	/**
	 * 配置异常处理器的作用范围(iocID.方法名或者iocID)
	 * @return
	 */
	String[] value() default {};

	/**
	 * 指定对Controller中所有方法的返回值处理策略<br>
	 * 1.Rest.NO(默认选项)：转发与重定向处理,只对返回值类型为String的结果进行处理<br>
	 *  &nbsp;&nbsp;&nbsp;
	 * a.转发到页面：无前缀 return page<br>
	 * 	&nbsp;&nbsp;&nbsp;
	 * b.转发到Controller方法:return forward:method<br>
	 *	&nbsp;&nbsp;&nbsp;
	 * c.重定向到页面：return page:pageing<br>
	 *	&nbsp;&nbsp;&nbsp;
	 * d.重定向到Controller方法：return redirect:method<br>
	 * 2.Rest.TXT：将返回值封装为txt格式，并返回给客户端<br>
	 * 3.Rest.JSON：将返回值封装为json格式，并返回给客户端<br>
	 * 4.Rest.XML：将返回值封装为xml格式，并返回给客户端
	 * @return
	 */
	Rest rest() default Rest.NO;
	
	boolean global() default true;
	
}
