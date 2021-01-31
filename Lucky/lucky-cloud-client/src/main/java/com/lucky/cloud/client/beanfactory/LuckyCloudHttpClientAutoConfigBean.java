package com.lucky.cloud.client.beanfactory;

import com.lucky.framework.annotation.Bean;
import com.lucky.framework.annotation.Configuration;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/31 上午12:39
 */
@Configuration
public class LuckyCloudHttpClientAutoConfigBean {

    @Bean
    public LuckyCloudHttpClientBeanFactory luckyCloudHttpClientBeanFactory(){
        return new LuckyCloudHttpClientBeanFactory();
    }
}
