package com.lucky.jacklamb.framework.annotation;

import java.lang.annotation.*;

/**
 * 定义一个Service组件
 * @author fk-7075
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component(type = "service")
public @interface Service {
	
	/**
	 * 为该Service组件指定一个唯一ID，默认会使用[首字母小写类名]作为组件的唯一ID
	 * @return
	 */
	String value() default "";
}
