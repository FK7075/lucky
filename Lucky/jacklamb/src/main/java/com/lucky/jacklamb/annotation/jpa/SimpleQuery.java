package com.lucky.jacklamb.annotation.jpa;

import java.lang.annotation.*;

/**
 * 取消全映射机制
 * @author fk7075
 * @version 1.0
 * @date 2020/10/26 12:20
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimpleQuery {
}
