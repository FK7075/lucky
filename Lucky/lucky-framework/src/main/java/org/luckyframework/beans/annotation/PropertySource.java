package org.luckyframework.beans.annotation;

import java.lang.annotation.*;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/12 0012 15:49
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(PropertySources.class)
public @interface PropertySource {

    String[] value();

    boolean ignoreResourceNotFound() default false;

    String encoding() default "UTF-8";
}
