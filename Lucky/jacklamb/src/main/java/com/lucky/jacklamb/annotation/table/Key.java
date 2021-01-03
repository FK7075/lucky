package com.lucky.jacklamb.annotation.table;

import java.lang.annotation.*;

/**
 * 外键标识
 * @author fk-7075
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Keys.class)
public @interface Key {

	/**
	 * 数据源
	 * @return
	 */
	String dbname() default "UNIVERSAL";
	
	/**
	 * 外键字段名
	 * @return
	 */
	String value() default "";
	
	/**
	 * 设置建表时的字段长度,默认100
	 * @return
	 */
	int length() default 100;
	
	/**
	 * 建表时是否允许该字段为NULL，默认true
	 * @return
	 */
	boolean allownull() default true;
	
	/**
	 * 外键所指向主表对应的实体类Class
	 * @return
	 */
	Class<?> pojo();
}
