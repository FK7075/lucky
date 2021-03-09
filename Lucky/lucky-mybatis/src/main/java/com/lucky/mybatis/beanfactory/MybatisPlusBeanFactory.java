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
        Map<String, List<Class<?>>> mapperClassesMap = dbnameGroup();
        Configuration configuration=new MybatisConfiguration();
        configurationSetting(configuration);
        for (LuckyDataSource luckyDataSource : allDataSource) {
            String dbname = luckyDataSource.getDbname();
            Environment environment = new Environment("development", getJdbcTransactionFactory(), luckyDataSource.createDataSource());
            configuration.setEnvironment(environment);
            List<Class<?>> mapperClasses = mapperClassesMap.get(dbname);
            Resource[] resources;
            String mapperLocations = mybatisConfig.getMapperLocations();
            if(mapperLocations!=null){
                try {
                    resources = resourcePatternResolver.getResources(mybatisConfig.getMapperLocations());
                    for (Resource resource : resources) {
                        new XMLMapperBuilder(resource.getInputStream(),configuration,resource.getDescription(),configuration.getSqlFragments()).parse();
                    }
                }catch (IOException e){
                    throw new BeanFactoryInitializationException(e,"Mybatis BeanFactory initialization failed！Error loading mapper resource file!");
                }
            }
            if(mapperClasses!=null){
                for (Class<?> mapperClass : mapperClasses) {
                    try {
                        configuration.addMapper(mapperClass);
                    }catch (Exception ignored){}
                    MybatisSqlSessionFactoryBuilder factoryBuilder = new MybatisSqlSessionFactoryBuilder();
                    SqlSessionFactory sessionFactory
                            = factoryBuilder.build(configuration);
                    SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(sessionFactory);
                    String beanName = getBeanId(mapperClass);
                    lifecycleMange.beforeCreatingInstance(mapperClass,beanName,TYPE);
                    mappers.add(new Module(beanName,TYPE,sqlSessionTemplate.getMapper(mapperClass)));
                }
            }
        }
        return mappers;
    }


    private String getBeanId(Class<?> mapperClass){
        String id = mapperClass.getAnnotation(Mapper.class).id();
        return Assert.isBlankString(id)? Namer.getBeanName(mapperClass):id;
    }
}
