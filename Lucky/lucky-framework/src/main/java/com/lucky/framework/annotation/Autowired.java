package com.lucky.framework.annotation;

import java.lang.annotation.*;

/**
 * DI相关的注解
 * @author fk-7075
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
	
	/**
	 * 指定要注入对象的ID，不指定则会启动类型扫描机制进行自动注入
	 * @return
	 */
	String value() default "";
}
