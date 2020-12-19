package com.lucky.junit.annotation;

import com.lucky.aop.annotation.Transaction;
import java.lang.annotation.*;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/19 下午3:13
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Transaction
@LuckyBootTest
public @interface LuckyBootTransactionTest {
}
