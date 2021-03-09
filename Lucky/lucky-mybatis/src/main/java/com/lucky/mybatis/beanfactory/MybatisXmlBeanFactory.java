package com.lucky.mybatis.beanfactory;

import com.lucky.framework.annotation.Component;
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
@Component
public class MybatisXmlBeanFactory extends BaseMybatisFactory {

    @Override
    public List<Module> createBean() {
        List<Module> mappers = super.createBean();
        BufferedReader reader = Resources.getReader("/mybatis.xml");
        SqlSessionFactory sqlSessionFactory
                =new SqlSessionFactoryBuilder().build(reader);
        List<Class<?>> mapperPlugins = getPluginByAnnotation(Mapper.class);
        for (Class<?> mapperPlugin : mapperPlugins) {
            SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(sqlSessionFactory);
            String beanId = getBeanId(mapperPlugin);
            lifecycleMange.beforeCreatingInstance(mapperPlugin,beanId,TYPE);
            mappers.add(new Module(getBeanId(mapperPlugin),TYPE,sqlSessionTemplate.getMapper(mapperPlugin)));
        }
        return mappers;
    }
}
