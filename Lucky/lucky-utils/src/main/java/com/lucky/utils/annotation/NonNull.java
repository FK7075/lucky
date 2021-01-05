package com.lucky.utils.annotation;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/5 0005 11:37
 * @see NonNullApi
 * @see NonNullFields
 * @see Nullable
 */

@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Nonnull
@TypeQualifierNickname
public @interface NonNull {
}
