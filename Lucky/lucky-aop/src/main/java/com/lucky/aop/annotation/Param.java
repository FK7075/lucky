package com.lucky.aop.annotation;

import java.lang.annotation.*;

/**
 * 设置增强参数
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
	
	/**
	 * 设置增强方法的参数<br>
	 * <p>
	 *     value有4种指定的写法,不同的前缀代表不同的含义:<p/>
	 * <p>
	 *     ref:表示将IOC容器中ID为id的组件设置对应位置的参数，eg:params={"ref:beanId"}</p>
	 * <p>
	 *     ind:表示将真实方法作为增强方法的参数，eg:params={"ind:index"},表示将真实方法参数列表中的第index个设置为增强方法的参数</p>
	 * <p>
	 *     无前缀:表示将真实方法的参数作为增强方法的参数，eg:params={"lucky"},表示将真实方法参数列表中的lucky参数设置为增强方法的参数<br></p>
	 * <p>
	 *     return:表示将真实方法的返回值设置为增强方法的参数</p>
	 * <p>
	 *     runtime:表示真实方法执行所用的时间</p>
	 * @return value
	 */
	String value() default "return";

}
