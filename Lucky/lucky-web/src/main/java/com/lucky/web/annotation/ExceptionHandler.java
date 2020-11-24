package com.lucky.web.annotation;

import java.lang.annotation.*;

/**
 * 定义要处理的异常
 * @author fk7075
 * @version 1.0.0
 * @date 2020/7/19 11:33 下午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionHandler {

     Class<? extends Throwable>[] value() default Throwable.class;
}
