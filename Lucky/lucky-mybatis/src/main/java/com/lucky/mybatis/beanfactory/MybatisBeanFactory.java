package com.lucky.mybatis.beanfactory;

import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.datasource.sql.LuckyDataSourceManage;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.framework.container.factory.Namer;
import com.lucky.mybatis.annotation.Mapper;
import com.lucky.mybatis.conf.MybatisConfig;
import com.lucky.utils.base.Assert;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/7 0007 15:45
 */
public class MybatisBeanFactory extends IOCBeanFactory {

    private final static String TYPE="mybatis-mapper";
    public MybatisBeanFactory(){
    }

    @Override
    public List<Module> createBean() {
        List<Module> mappers = super.createBean();
        //注册数据源
        getBeanByClass(LuckyDataSource.class)
                .stream()
                .map(m->(LuckyDataSource)m.getComponent())
                .forEach(LuckyDataSourceManage::addLuckyDataSource);

        //创建SqlCore对象
        List<LuckyDataSource> allDataSource = LuckyDataSourceManage.getAllDataSource();
        Map<String, List<Class<?>>> mapperClassesMap = dbnameGroup();
        for (LuckyDataSource luckyDataSource : allDataSource) {
            String dbname = luckyDataSource.getDbname();
            Configuration configuration=new Configuration();
            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("development", transactionFactory, luckyDataSource.createDataSource());
            configuration.setEnvironment(environment);
            List<Class<?>> mapperClasses = mapperClassesMap.get(dbname);
            if(mapperClasses!=null){
                mapperClasses.stream().forEach(configuration::addMapper);
                SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
                mapperClasses.stream().map(c->new Module(getBeanId(c),TYPE,sessionFactory.openSession().getMapper(c)))
                        .forEach(mappers::add);

            }
        }
        return mappers;
    }

    private Map<String, List<Class<?>>> dbnameGroup(){
        return getPluginByAnnotation(Mapper.class).stream()
                .collect(Collectors.groupingBy(c->c.getAnnotation(Mapper.class).dbname()));
    }

    private String getBeanId(Class<?> mapperClass){
        String id = mapperClass.getAnnotation(Mapper.class).id();
        return Assert.isBlankString(id)? Namer.getBeanName(mapperClass):id;
    }
}
