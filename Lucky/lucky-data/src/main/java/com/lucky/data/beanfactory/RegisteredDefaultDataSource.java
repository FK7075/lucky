package com.lucky.data.beanfactory;

import com.lucky.datasource.sql.HikariCPDataSource;
import com.lucky.datasource.sql.LuckyDataSourceManage;
import com.lucky.framework.annotation.Bean;
import com.lucky.framework.annotation.Configuration;

/**
 * 注册默认的连接池类型（HikariCP）
 * @author fk
 * @version 1.0
 * @date 2021/1/8 0008 16:01
 */
@Configuration(priority = 4)
public class RegisteredDefaultDataSource {

    @Bean
    public void registered(){
        LuckyDataSourceManage.registerPool(HikariCPDataSource.class);
    }
}
