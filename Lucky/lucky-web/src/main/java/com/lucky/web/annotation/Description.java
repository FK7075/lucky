package com.lucky.web.annotation;

import java.lang.annotation.*;

/**
 * 描述一个Mapping的详细信息
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/27 上午2:38
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Description {
    /** 功能描述*/
    String desc() default "";
    /** 作者*/
    String author() default "";
    /** 版本*/
    String version() default "";
    /** 注释，对方法的详细描述*/
    String comment() default "";
}
