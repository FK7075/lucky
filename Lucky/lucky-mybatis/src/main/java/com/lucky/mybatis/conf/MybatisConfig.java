package com.lucky.mybatis.conf;

import com.lucky.framework.confanalysis.LuckyConfig;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.stream.Stream;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/7 0007 15:48
 */
public class MybatisConfig extends LuckyConfig {

    private static MybatisConfig config;
    private Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void addMapper(Class<?>...mapper){
        Stream.of(mapper).forEach(configuration::addMapper);
    }

    private MybatisConfig(){};

    public static MybatisConfig defaultMybatisConfig(){
        if(config==null){
            config=new MybatisConfig();
            Configuration configuration=new Configuration();
            DataSource dataSource = null;
            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("development", transactionFactory, dataSource);
            configuration.setEnvironment(environment);
        }
        return config;
    }

    public static MybatisConfig getMybatisConfig(){
        MybatisConfig webConfig = defaultMybatisConfig();
        if(webConfig.isFirst()){
            YamlParsing.loadMyBatis(webConfig);
        }
        return webConfig;
    }
}
