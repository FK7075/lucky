package com.lucky.web.annotation;

import com.lucky.web.httpclient.callcontroller.Api;
import org.omg.CORBA.Object;

import java.lang.annotation.*;

/**
 * 格式校验，匹配一段正则表达式
 * @author fk-7075
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Check {


	/**
	 * 正则表达式
	 * @return
	 */
	String[] value();

	/**
	 * 校验失败后的提示信息
	 * @return
	 */
	String error() default "";
}
