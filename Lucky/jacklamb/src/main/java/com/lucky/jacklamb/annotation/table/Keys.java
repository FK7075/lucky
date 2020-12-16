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
public @interface Keys {
	Key[] value();
}
