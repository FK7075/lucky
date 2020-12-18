package com.lucky.junit.annotation;

import com.lucky.junit.core.LuckyExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/16 15:41
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith({LuckyExtension.class})
public @interface LuckyBootTest {

    Class<?> rootClass() default Void.class;
}
