package com.lucky.web.annotation;

import java.lang.annotation.*;

/**
 * @author fk7075
 * @time 2020-5-28
 * 声明该方法在服务器启动时将会被执行
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InitRun {

    /**
     * 执行优先级
     * @return
     */
    int value() default 5;

}
