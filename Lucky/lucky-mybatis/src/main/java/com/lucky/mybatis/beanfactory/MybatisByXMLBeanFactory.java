package com.lucky.mybatis.beanfactory;

import com.lucky.framework.annotation.Configuration;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.framework.container.factory.Namer;
import com.lucky.mybatis.annotation.Mapper;
import com.lucky.mybatis.proxy.SqlSessionTemplate;
import com.lucky.utils.base.Assert;
import com.lucky.utils.file.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.BufferedReader;
import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/11 0011 9:43
 */
@Configuration
public class MybatisByXMLBeanFactory extends IOCBeanFactory {


    private final static String TYPE="mybatis-mapper";

    @Override
    public List<Module> createBean() {
        List<Module> mappers = super.createBean();
        BufferedReader reader = Resources.getReader("/mybatis.xml");
        SqlSessionFactory sqlSessionFactory
                =new SqlSessionFactoryBuilder().build(reader);
        List<Class<?>> mapperPlugins = getPluginByAnnotation(Mapper.class);
        for (Class<?> mapperPlugin : mapperPlugins) {
            SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(sqlSessionFactory);
            Object mapper=sqlSessionTemplate.getMapper(mapperPlugin);
            mappers.add(new Module(getBeanId(mapperPlugin),TYPE,mapper));
        }
        return mappers;
    }

    private String getBeanId(Class<?> mapperClass){
        String id = mapperClass.getAnnotation(Mapper.class).id();
        return Assert.isBlankString(id)? Namer.getBeanName(mapperClass):id;
    }
}
