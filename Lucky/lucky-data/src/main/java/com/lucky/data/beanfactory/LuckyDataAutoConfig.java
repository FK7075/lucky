package com.lucky.data.beanfactory;

import com.lucky.data.aspect.TransactionPoint;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.annotation.Bean;
import com.lucky.framework.annotation.Configuration;
import com.lucky.jacklamb.annotation.table.Table;
import com.lucky.jacklamb.annotation.table.Tables;
import com.lucky.jacklamb.datasource.LuckyDataSource;
import com.lucky.jacklamb.datasource.LuckyDataSourceManage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

//    @Bean
    public void addAutoCreateTables(){
        List<LuckyDataSource> allDataSource = LuckyDataSourceManage.getAllDataSource();
        Set<Class<?>> autoTables = AutoScanApplicationContext.create().getClasses(Table.class, Tables.class);
        for (LuckyDataSource luckyDataSource : allDataSource) {
            Set<String> autoTablesStr = autoTables.stream().map(c -> c.getName()).collect(Collectors.toSet());
            luckyDataSource.setCreateTable(autoTablesStr);
        }
    }
}
