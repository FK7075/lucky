package com.lucky.utils.conversion.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mappings {
    Mapping[] value();
}
