package com.lucky.jacklamb.annotation.jpa;

import java.lang.annotation.*;

/**
 * 全映射查询注解，被此注解标注的Mapper接口方法执行查询操作时会触发全映射机制
 * @author fk7075
 * @version 1.0
 * @date 2020/10/26 9:25
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FullMapQuery {
}
