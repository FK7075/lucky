package com.lucky.jacklamb.annotation.mapper;

import java.lang.annotation.*;

/**
 * ORM的Mapper接口中使用，定义一个删除的数据库操作
 * 	value：设置预编译的SQL （eg: DELETE * FROM book WHERE bname=? AND price>?）
 * @author fk-7075
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Delete {
	String value() default "";
	
	/**
	 * 是否启用[Class+ID]删除
	 * @return
	 */
	boolean byid() default false;

	boolean batch() default false;
}
