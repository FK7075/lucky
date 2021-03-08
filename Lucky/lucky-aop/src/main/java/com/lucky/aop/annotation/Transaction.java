package com.lucky.aop.annotation;

import java.lang.annotation.*;

/**
 * 数据库事务
 * 默认情况下，只对RuntimeException和Error类型的异常进行回滚操作
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transaction {

    /**
     * 设置事务的隔离级别
     * @return isolationLevel
     */
    int isolationLevel() default -1;

    /**
     * 设置指定需要处理的异常类型
     * @return
     */
    Class<? extends Throwable>[] rollbackFor() default {};

    /**
     * 设置忽略的异常类型
     * @return
     */
    Class<? extends Throwable>[] noRollbackFor() default {};

}
