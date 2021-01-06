package com.lucky.web.interceptor;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.annotation.Plugin;

import java.lang.annotation.*;

/**
 * 定义一个拦截器
 * @author fk
 * @version 1.0
 * @date 2021/1/5 0005 16:20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component(type = "interceptor")
@Plugin
public @interface Interceptor {
    /** 指定要拦截的路径*/
    String[] value() default {};
    /** 指定要排除的路径*/
    String[] excludePath() default {};
    /** 指定拦截器执行的优先级*/
    double priority() default 5;
}
