package com.lucky.jacklamb.annotation.table;

import java.lang.annotation.*;

/**
 * 表名映射
 * @author fk-7075
 *
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {

	/**
	 * 数据源
	 * @return
	 */
	String dbname() default "UNIVERSAL";
	
	/**
	 * 表名映射
	 * @return
	 */
	String value() default "";
	
	/**
	 * 别名,只在使用SqlCore的query()方法时有效
	 * @return
	 */
	String alias() default "";
	
	/**
	 * 添加主键索引[@Table(primary="bid")]
	 * @return
	 */
	String primary() default "";
	
	/**
	 * 添加普通索引[@Table(index={"bname","price"})]
	 * @return
	 */
	String[] index() default {};
	
	/**
	 * 添加唯一值索引[@Table(unique={"bname","price"})]
	 * @return
	 */
	String[] unique() default {};
	
	/**
	 * 添加全文索引[@Table(fulltext={"bname","price"})]
	 * @return
	 */
	String[] fulltext() default {};
	
	/**
	 * 子表级联删除,默认false
	 * @return
	 */
	boolean cascadeDelete() default false;
	
	/**
	 * 子表级联更新,默认false
	 * @return
	 */
	boolean cascadeUpdate() default false;
}
