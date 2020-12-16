package com.lucky.web.beanfactory;

import com.lucky.framework.annotation.Bean;
import com.lucky.framework.annotation.Configuration;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/16 0016 17:04
 */
@Configuration
public class LuckyWebAutoConfig {

    @Bean
    public LuckyWebBeanFactory luckyWebBeanFactory(){
        return new LuckyWebBeanFactory();
    }

    @Bean
    public WebDestroy webDestroy(){
        return new WebDestroy();
    }
}
