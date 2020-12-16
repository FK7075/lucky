package com.lucky.jacklamb.annotation.table;

import java.lang.annotation.*;

/**
 * 标注属性不参与SQL的生产
 * @author fk-7075
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoColumns {
	
	NoColumn[] value();

}