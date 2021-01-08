package com.lucky.data.beanfactory;

import com.lucky.data.aspect.TransactionPoint;
import com.lucky.framework.annotation.Bean;
import com.lucky.framework.annotation.Configuration;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/18 下午11:21
 */
@Configuration
public class LuckyDataAutoConfig {

    @Bean
    public TransactionPoint transactionPoint(){
        return new TransactionPoint();
    }

    @Bean
    public LuckyDataJacklambBeanFactory luckyDataJacklambBeanFactory(){
        return new LuckyDataJacklambBeanFactory();
    }

    @Bean
    public LuckyDataDestroy luckyDataDestroy(){
        return new LuckyDataDestroy();
    }
}
