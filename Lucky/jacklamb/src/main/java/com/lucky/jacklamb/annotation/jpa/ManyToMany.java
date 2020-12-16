package com.lucky.jacklamb.annotation.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ManyToMany {

    String value() default "";

    String joinTable();

    String joinColumnThis();

    String joinColumnTo();
}
