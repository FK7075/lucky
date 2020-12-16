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
public @interface Tables {
	Table[] value();
}
