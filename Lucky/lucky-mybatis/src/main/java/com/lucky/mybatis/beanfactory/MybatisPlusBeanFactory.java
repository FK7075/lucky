package com.lucky.mybatis.beanfactory;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.datasource.sql.LuckyDataSourceManage;
import com.lucky.framework.annotation.Component;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.BeanFactoryInitializationException;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.framework.container.factory.Namer;
import com.lucky.mybatis.annotation.Mapper;
import com.lucky.mybatis.conf.MybatisConfig;
import com.lucky.mybatis.proxy.SqlSessionTemplate;
import com.lucky.utils.base.Assert;
import com.lucky.utils.fileload.Resource;
import com.lucky.utils.fileload.ResourcePatternResolver;
import com.lucky.utils.fileload.resourceimpl.PathMatchingResourcePatternResolver;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/10 下午4:14
 */
@Component
public class MybatisPlusBeanFactory extends BaseMybatisFactory {


    @Override
    public List<Module> createBean() {
        List<Module> mappers = super.createBean();
        List<LuckyDataSource> allDataSource = getAllDataSource();
        Configuration configuration=new MybatisConfiguration();
        configurationSetting(configuration);
        for (LuckyDataSource luckyDataSource : allDataSource) {
            configuration.setEnvironment(new Environment("development", getJdbcTransactionFactory(), luckyDataSource.createDataSource()));
            List<MapperSource> mapperSources = getMapperLocations();
            if(mapperSources!=null){
                mapperSources.forEach(in->new XMLMapperBuilder(in.getIn(),configuration,in.getDescription(),configuration.getSqlFragments()).parse());
            }
            SqlSessionFactory sessionFactory = new MybatisSqlSessionFactoryBuilder().build(configuration);
            mappers.addAll(getMappers(sessionFactory,configuration,luckyDataSource.getDbname()));
        }
        return mappers;
    }
}
