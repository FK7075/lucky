package com.lucky.aop.beanfactory;

import com.lucky.framework.annotation.Bean;
import com.lucky.framework.annotation.Configuration;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/16 0016 17:35
 */
@Configuration
public class LuckyAopAutoConfig {

    @Bean
    public LuckyAopBeanFactory luckyAopBeanFactory(){
        return new LuckyAopBeanFactory();
    }
}
