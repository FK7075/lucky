package com.lucky.junit.annotation;

import com.lucky.aop.annotation.Transaction;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/19 下午2:57
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Test
@Tag("transaction")
@Transaction
public @interface TransactionTest {
}
