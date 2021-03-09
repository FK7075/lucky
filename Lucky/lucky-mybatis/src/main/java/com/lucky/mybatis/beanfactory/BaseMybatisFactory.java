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
import com.lucky.utils.base.ArrayUtils;
import com.lucky.utils.base.Assert;
import com.lucky.utils.fileload.Resource;
import com.lucky.utils.fileload.ResourcePatternResolver;
import com.lucky.utils.fileload.resourceimpl.PathMatchingResourcePatternResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/9 0009 18:38
 */
public abstract class BaseMybatisFactory extends IOCBeanFactory {

    protected final static String TYPE="mybatis-mapper";
    protected final static MybatisConfig mybatisConfig=MybatisConfig.getMybatisConfig();
    protected final ResourcePatternResolver resourcePatternResolver;

    public BaseMybatisFactory(){
        super();
        resourcePatternResolver=new PathMatchingResourcePatternResolver();
    }

    protected List<LuckyDataSource> getAllDataSource(){
        //将用户配置在IOC容器中的数据源注册到数据源管理器中
        getBeanByClass(LuckyDataSource.class)
                .stream()
                .map(m->(LuckyDataSource)m.getComponent())
                .forEach(LuckyDataSourceManage::addLuckyDataSource);
        List<LuckyDataSource> allDataSource = LuckyDataSourceManage.getAllDataSource();
        if(Assert.isEmptyCollection(allDataSource)){
            throw new BeanFactoryInitializationException("Mybatis BeanFactory initialization failed！ No data source is registered in the data source manager!");
        }
        return allDataSource;
    }

    protected TransactionFactory getJdbcTransactionFactory(){
        return new JdbcTransactionFactory();
    }

    protected void configurationSetting(Configuration configuration){
        if(configuration==null) return;
        configuration.setLogImpl(mybatisConfig.getLogImpl());
        configuration.setMapUnderscoreToCamelCase(mybatisConfig.isMapUnderscoreToCamelCase());
        mybatisConfig.getInterceptors().forEach(configuration::addInterceptor);
        if(mybatisConfig.getTypeAliasesPackage()!=null){
            configuration.getTypeAliasRegistry().registerAliases(mybatisConfig.getTypeAliasesPackage());
        }
        if(mybatisConfig.getVfsImpl()!=null){
            configuration.setVfsImpl(mybatisConfig.getVfsImpl());
        }
    }

    protected List<MapperSource> getMapperLocations(){
        String mapperLocations = mybatisConfig.getMapperLocations();
        if(mapperLocations==null) return null;
        try {
            Resource[] resources= resourcePatternResolver.getResources(mapperLocations);
            if(Assert.isEmptyArray(resources)) return null;
            List<MapperSource> mapperSources=new ArrayList<>(resources.length);
            for (Resource resource : resources) {
                mapperSources.add(new MapperSource(resource.getInputStream(),resource.getDescription()));
            }
            return mapperSources;
        } catch (IOException e) {
            throw new BeanFactoryInitializationException(e,"Mybatis BeanFactory initialization failed！Error obtain mapper resource file!");
        }
    }

    protected List<Module> getMappers(Configuration configuration,String dbname){
        List<Module> mappers=new ArrayList<>();
        List<Class<?>> mapperClasses = dbnameGroup().get(dbname);
        if (mapperClasses!=null){
            for (Class<?> mapperClass : mapperClasses) {
                try {
                    configuration.addMapper(mapperClass);
                }catch (Exception ignored){}
                SqlSessionFactory sessionFactory
                        = new SqlSessionFactoryBuilder().build(configuration);
                SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(sessionFactory);
                String beanName = getBeanId(mapperClass);
                lifecycleMange.beforeCreatingInstance(mapperClass,beanName,TYPE);
                mappers.add(new Module(beanName,TYPE,sqlSessionTemplate.getMapper(mapperClass)));
            }
        }
        return mappers;
    }

    protected Map<String, List<Class<?>>> dbnameGroup(){
        return getPluginByAnnotation(Mapper.class).stream()
                .collect(Collectors.groupingBy(c->c.getAnnotation(Mapper.class).dbname()));
    }

    private String getBeanId(Class<?> mapperClass){
        String id = mapperClass.getAnnotation(Mapper.class).id();
        return Assert.isBlankString(id)? Namer.getBeanName(mapperClass):id;
    }

    protected static class MapperSource{
        private InputStream in;
        private String description;

        public MapperSource(InputStream in, String description) {
            this.in = in;
            this.description = description;
        }

        public InputStream getIn() {
            return in;
        }

        public void setIn(InputStream in) {
            this.in = in;
        }

        public String getDescription() {
            return description;
        }
    }

}
