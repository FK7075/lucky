package com.lucky.quartz.annotation;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.annotation.Plugin;

import java.lang.annotation.*;

/**
 * 定义一个定时任务组件
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Plugin
@Component(type = "quartz_job")
public @interface QuartzJobs {

    /** 组件的唯一ID */
    String value() default "";
}
