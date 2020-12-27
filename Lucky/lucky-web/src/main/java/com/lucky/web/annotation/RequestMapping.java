package com.lucky.web.annotation;

import com.lucky.web.enums.RequestMethod;

import java.lang.annotation.*;

/**
 * 定义一个URL映射(支持Rest风格的URL)<br>
 * 1.普通类型的URL映射<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;
 * -@RequestMapping("/server/query")<br>
 * 2.Rest风格的URL映射<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;
 * -@RequestMapping("/server/query/name/#{name}/sex/#{sex}")<br>
 * 3.带有特殊符号的URL映射<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;
 * a."?":匹配一个任意的字符串<br>&nbsp;&nbsp;&nbsp;&nbsp;
 * -@RequestMapping("/server/?/query")<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;
 * b.xxx*:匹配一个以xxx开头的字符串<br>&nbsp;&nbsp;&nbsp;&nbsp;
 * -@RequestMapping("/server/admin_* /query")<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;
 * c.*xxx：匹配一个以xxx结尾的字符串<br>&nbsp;&nbsp;&nbsp;&nbsp;
 * -@RequestMapping("/server/*_admin/query")<br>
 * 
 * @author fk-7075
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
	
	/**
	 * 定义一个url请求映射
	 * @return
	 */
	String value() default "/";
	
	/**
	 * 指定一些合法访问的ip地址，来自其他ip地址的请求将会被拒绝
	 * @return
	 */
	String[] ip() default {};
	
	/**
	 * 指定一些合法访问的ip段，来自其他ip地址的请求将会被拒绝
	 * @return
	 */
	String[] ipSection() default {};

	
	/**
	 * 定义该映射支持的请求类型，默认支持POST GET PUT DELETE
	 * @return
	 */
	RequestMethod[] method() default { RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE };

}
