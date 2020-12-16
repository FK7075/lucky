package com.lucky.jacklamb.annotation.mapper;

import java.lang.annotation.*;

/**
 * ORM的Mapper接口中使用，定义一个查询的数据库操作
 * 	value：设置预编译的SQL (eg: SELECT * FROM book WHERE bid=?)
 * 	sResults:(showResults)设置所要查询的字段
 * 	hResults:(hiddenResults)设置要隐藏的字段
 * 	注：sResults、hResults不可同时是使用
 * @author fk-7075
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Select {
	String value() default "";
	String[] sResults() default {};
	String[] hResults() default {};
	boolean byid() default false;
}
