package com.lucky.mybatis.beanfactory;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.IOCBeanFactory;

import java.util.List;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/10 下午4:14
 */
public class MybatisPlusBeanFactory extends IOCBeanFactory {


    @Override
    public List<Module> createBean() {
        List<Module> mappers = super.createBean();
        MybatisSqlSessionFactoryBean factoryBean=new MybatisSqlSessionFactoryBean();
        List<Module> luckydatasources = getBeanByClass(LuckyDataSource.class);

//        factoryBean.set
        return super.createBean();
    }
}
