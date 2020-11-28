package com.lucky.aop.annotation;

import java.lang.annotation.*;

/**
 * 操作日志
 * @author fk7075
 * @version 1.0.0
 * @date 2020/10/3 11:06 上午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {

    /**
     * 操作模块
     * @return module
     */
    String module() default "";
    /**
     * 操作类型
     * @return  type
     */
    String type() default "";
    /**
     * 操作说明
     * @return desc
     */
    String desc() default "";
}
