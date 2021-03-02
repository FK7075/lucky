package com.lucky.mybatis.beanfactory;

import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.datasource.sql.LuckyDataSourceManage;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.BeanFactoryInitializationException;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.framework.container.factory.Namer;
import com.lucky.mybatis.annotation.Mapper;
import com.lucky.mybatis.conf.MybatisConfig;
import com.lucky.mybatis.proxy.SqlSessionTemplate;
import com.lucky.utils.base.Assert;
import com.lucky.utils.file.*;
import com.lucky.utils.fileload.Resource;
import com.lucky.utils.fileload.ResourcePatternResolver;
import com.lucky.utils.fileload.resourceimpl.PathMatchingResourcePatternResolver;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.IOException;
import java.io.InputStream;
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
    private final static MybatisConfig mybatisConfig=MybatisConfig.getMybatisConfig();
    private final ResourcePatternResolver resourcePatternResolver;
    public MybatisBeanFactory(){
        resourcePatternResolver=new PathMatchingResourcePatternResolver();
    }

    @Override
    public List<Module> createBean() {
        List<Module> mappers = super.createBean();
        //将用户配置在IOC容器中的数据源注册到数据源管理器中
        getBeanByClass(LuckyDataSource.class)
                .stream()
                .map(m->(LuckyDataSource)m.getComponent())
                .forEach(LuckyDataSourceManage::addLuckyDataSource);

        List<LuckyDataSource> allDataSource = LuckyDataSourceManage.getAllDataSource();
        if(Assert.isEmptyCollection(allDataSource)){
            throw new BeanFactoryInitializationException("Mybatis BeanFactory initialization failed！ No data source is registered in the data source manager!");
        }
        Map<String, List<Class<?>>> mapperClassesMap = dbnameGroup();
        Configuration configuration=new Configuration();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        configuration.setLogImpl(mybatisConfig.getLogImpl());
        configuration.setMapUnderscoreToCamelCase(mybatisConfig.isMapUnderscoreToCamelCase());
        mybatisConfig.getInterceptors().forEach(configuration::addInterceptor);
        if(mybatisConfig.getTypeAliasesPackage()!=null){
            configuration.getTypeAliasRegistry().registerAliases(mybatisConfig.getTypeAliasesPackage());
        }
        if(mybatisConfig.getVfsImpl()!=null){
            configuration.setVfsImpl(mybatisConfig.getVfsImpl());
        }
        for (LuckyDataSource luckyDataSource : allDataSource) {
            String dbname = luckyDataSource.getDbname();
            List<Class<?>> mapperClasses = mapperClassesMap.get(dbname);
            Environment environment = new Environment("development", transactionFactory, luckyDataSource.createDataSource());
            configuration.setEnvironment(environment);
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
                    SqlSessionFactory sessionFactory
                            = new SqlSessionFactoryBuilder().build(configuration);
                    SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(sessionFactory);
                    mappers.add(new Module(getBeanId(mapperClass),TYPE,sqlSessionTemplate.getMapper(mapperClass)));
                }
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
