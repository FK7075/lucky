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
	 *     1. value有4种指定的写法,不同的前缀代表不同的含义:<p/><p>
	 *     2. ref:表示将IOC容器中ID为id的组件设置对应位置的参数。例如：@Param("ref:beanId")</p><p>
	 *     3. ind:表示将真实方法作为增强方法的参数,表示将真实方法参数列表中的第index个设置为增强方法的参数(注意：此处的索引是从`1`开始的)。例如：@Param("ind:1")</p><p>
	 *     4. 无前缀:表示将真实方法的参数作为增强方法的参数,表示将真实方法参数列表中参数名为lucky参数设置为增强方法的参数。例如：@Param("lucky")</p><p>
	 *     5. return:表示将真实方法的返回值设置为增强方法的参数(固定写法)。例如：@Param("return")</p><p>
	 *     6. runtime:表示真实方法执行所用的时间(固定写法)。例如：@Param("runtime")
	 *  </p>
	 * @return value
	 */
	String value() default "return";

}
