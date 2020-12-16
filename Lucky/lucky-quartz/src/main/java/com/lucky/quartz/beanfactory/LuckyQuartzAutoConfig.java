package com.lucky.quartz.beanfactory;

import com.lucky.framework.annotation.Bean;
import com.lucky.framework.annotation.Configuration;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/16 0016 17:37
 */
@Configuration
public class LuckyQuartzAutoConfig {

    @Bean
    public LuckyQuartzBeanFactory luckyQuartzBeanFactory(){
        return new LuckyQuartzBeanFactory();
    }
}
