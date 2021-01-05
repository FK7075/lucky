package com.lucky.framework.annotation;

import java.lang.annotation.*;

/**
 * 注册一个Component组件，需要配合@BeanFactory注解使用
 * @author fk-7075
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
	
	/**
	 * 为该Component组件指定一个唯一ID，默认会使用[类名.方法名]作为组件的唯一ID
	 * @return
	 */
	String value() default "";

	String type() default "component";

	double priority() default 5;

}
