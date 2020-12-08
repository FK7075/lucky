package com.lucky.quartz.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Job {

    /** 任务的唯一ID */
    String id() default "";

    /** 使用Cron表达式构建一个触发器 */
    String cron() default "";

    /**
     * [固定延时触发] 单位：ms
     * 在主动调用该注解标注的方法时，延迟fixedDela毫秒后执行一次后结束。
     */
    long fixedDelay() default -1L;

    /*
        [循环触发] interval + count
        interval：任务执行的间隔时间                       默认：1秒
        count：循环执行的次数，值小于1时表示无限循环的执行     默认：-1
     */
    /** 任务循环执行的时间间隔，默认为1秒。单位：ms */
    long interval() default 1000L;

    /** 该定时任务执行的次数，配合interval使用时生效。取值-1时任务会无限的执行*/
    int count() default -1;

    //dy的优先级较大

    /**
     * 动态cron，需要到方法参数中去获取具体的值
     */
    String dyCron() default "";

    /**
     * 动态fixedDelay，需要到方法参数中去获取具体的值
     */
    String dyDelay() default "";

    /**
     * 动态interval，需要到方法参数中去获取具体的值
     */
    String dyInterval() default "";

    /**
     * 动态count，需要到方法参数中去获取具体的值
     */
    String dyCount() default "";

    /**
     * 只运行最后一次调用，之前调用的任务将会被停止
     */
    boolean onlyLast() default false;

    /**
     * 仅在第一次被调用时被代理为定时任务，之后的调用不执行代理
     */
    boolean onlyFirst() default false;

    /**
     *是否允许任务并发执行
     */
    boolean parallel() default true;

}