package com.lucky.mybatis.annotation;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.annotation.Plugin;

import java.lang.annotation.*;

/**
 * 标注在Mapper接口上，注入一个java类型的mapper配置文件 java配置文件的SQl配置规则：<br/>
 * 每个没有使用注解的mapper接口方法都可以在配置类中绑定一组执行SQL，<br/>形式为一个String类型变量名+"特定SQL"
 * eg：<br/>
 * 普通模式：正常SQL->[SELECT * FROM book WHERE bid=?]<br/>
 * 开启基于非空检查的动态sql模式：C:SQL->[C:SELECT * FROM book WHERE bid=? AND bprice=?]<br/>
 * 
 * @author fk-7075
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Plugin
@Component(type = "mybatis-mapper")
public @interface Mapper {

	/**
	 * 为该Mapper组件指定一个唯一ID，默认会使用[首字母小写类名]作为组件的唯一ID
	 * @return
	 */
	String id() default "";

	/**
	 * Mapper接口所使用的数据源，默认defaultDB
	 * @return
	 */
	String dbname() default "defaultDB";
}
