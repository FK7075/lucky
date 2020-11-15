package com.lucky.jacklamb.framework.annotation;

import java.lang.annotation.*;

/**
 * 定义一个Repository组件
 * @author fk-7075
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component(type = "repository")
public @interface Repository {
	
	/**
	 * 为该Repository组件指定一个唯一ID，默认会使用[首字母小写类名]作为组件的唯一ID
	 * @return
	 */
	String value() default "";
}
