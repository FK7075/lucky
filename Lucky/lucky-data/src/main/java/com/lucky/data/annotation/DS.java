package com.lucky.data.annotation;

import java.lang.annotation.*;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/12 0012 9:15
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DS {

    /**
     * 标识数据源的参数名
     * @return
     */
    String value() default "";

    String dbParam() default "";
}
