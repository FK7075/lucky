package com.lucky.jacklamb.annotation.table;

import java.lang.annotation.*;

/**
 * 主键映射
 * 
 * @author fk-7075
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {

	/**
	 * 数据源
	 * @return
	 */
	String dbname() default "UNIVERSAL";
	
	/**
	 * 标识主键，设置映射名
	 * @return
	 */
	String value() default "";
	
	/**
	 * 设置类型<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;
	 * Type.DEFAULT(默认):普通主键<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;
	 * Type.AUTO_INT:自增的INT主键 <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;
	 * AUTO_UUID：自增的UUID主键
	 * @return
	 */
//	PrimaryType type() default PrimaryType.DEFAULT;
	
	/**
	 * 设置建表时的字段长度,默认100
	 * @return
	 */
	int length() default 100;
}
