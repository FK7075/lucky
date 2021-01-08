package com.lucky.mybatis.beanfactory;

import com.lucky.framework.annotation.Bean;
import com.lucky.framework.annotation.Configuration;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/8 0008 18:53
 */
@Configuration
public class MybatisAutoConfigBeans {

    @Bean
    public MybatisBeanFactory mybatisBeanFactory(){
        return new MybatisBeanFactory();
    }

    @Bean
    public LuckyDataDestroy luckyDataDestroy(){
        return new LuckyDataDestroy();
    }
}
