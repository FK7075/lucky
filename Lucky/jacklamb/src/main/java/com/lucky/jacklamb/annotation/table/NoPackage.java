package com.lucky.jacklamb.annotation.table;

import java.lang.annotation.*;

/**
 * 标注属性不参与SQL的包装
 * @author fk-7075
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(NoPackages.class)
public @interface NoPackage {
	
	String value() default "UNIVERSAL";

}