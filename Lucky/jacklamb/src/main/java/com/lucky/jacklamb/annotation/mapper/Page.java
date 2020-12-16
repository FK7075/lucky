package com.lucky.jacklamb.annotation.mapper;

import java.lang.annotation.*;

/**
 * 定义一个分页参数，使用@Page标注的页码参数会自动转化为开始位置参数[注意：两个分页参数必须写在参数列表的最后]
 * @author fk-7075
 *
 */
@Target({ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Page {
	String value() default "";
}
