package com.lucky.web.annotation;

import java.lang.annotation.*;

/**
 * 
 * 用于处理跨域的问题
 * 前端页面代码里我们在ajax请求里面带上 xhrFields: {withCredentials:true}这个属性，表示提供cookie信息,即可实现跨域的session共享问题.
 * @author fk-7075
 *
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrossOrigin {
	
	/**
	 * 设置允许可访问的域列表(默认为所有域)
	 * @return
	 */
	String[] value() default {};

	/**
	 * 设置允许可访问的域列表(默认为所有域)
	 * @return
	 */
	String[] origins() default {};
	
	/**
	 * 设置响应头中允许访问的header,默认为空
	 * @return
	 */
	String exposedHeaders() default "";
	
	/**
	 * 设置允许请求头重的header，默认都支持
	 * @return
	 */
	String allowedHeaders() default "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token";
	
	/**
	 * 设置准备响应前的缓存持续的最大时间（以秒为单位，默认为1800s）
	 * @return
	 */
	int maxAge() default 1800;
	
	/**
	 * 设置请求支持的方法(默认：POST, GET, OPTIONS, DELETE)
	 * @return
	 */
	String method() default "POST, GET, OPTIONS, DELETE";
	
	/**
	 * 设置是否允许cookie随请求发送(默认为true)
	 * @return
	 */
	boolean allowCredentials() default true;
	
}
