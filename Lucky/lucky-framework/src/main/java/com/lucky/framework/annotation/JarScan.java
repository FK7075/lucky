package com.lucky.framework.annotation;

import java.lang.annotation.*;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/7 0007 8:56
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(JarScans.class)
public @interface JarScan {

    /** 要加载Jar包的GroupId*/
    String groupId() default "";

    /**
     * Jar文件的位置
     *  1.classpath: 开头表示加载classpath下的jar文件
     *  2.无前缀    : 表示绝对路径
     * @return
     */
    String jarPath() default "";

    /**
     * 描述Jar文件的json文件位置
     *  1.classpath: 开头表示加载classpath下的jar文件
     *  2.无前缀    : 表示绝对路径
     * @see LuckyBootApplication{jarExpand}
     * @return
     */
    String jsonFile() default "";
}
