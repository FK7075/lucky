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
public @interface Columns {
	Column[] value();
}
