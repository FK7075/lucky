package com.lucky.jacklamb.annotation.table;

import java.lang.annotation.*;

/**
 * 确定类属性与表字段的映射关系
 * 
 * @author fk-7075
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {

	/**
	 * 数据源
	 * @return
	 */
	String dbname() default "UNIVERSAL";
	
	/**
	 * 属性对应数据库中的字段名
	 * @return
	 */
	String value() default "";
	
	/**
	 * 建表时该字段的长度,默认100
	 * @return
	 */
	int length() default 100;
	
	/**
	 * 建表时是否允许该字段为NULL，默认true
	 * @return
	 */
	boolean allownull() default true;
}
