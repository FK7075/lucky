package com.lucky.utils.annotation;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.*;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/5 0005 11:38
 * @see NonNullFields
 * @see Nullable
 * @see NonNull
 */

@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Nonnull
@TypeQualifierDefault({ElementType.METHOD, ElementType.PARAMETER})
public @interface NonNullApi {
}