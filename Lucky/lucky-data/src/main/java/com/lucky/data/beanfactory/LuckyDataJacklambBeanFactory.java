package com.lucky.data.beanfactory;

import com.lucky.data.annotation.Mapper;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.framework.container.factory.Namer;
import com.lucky.jacklamb.annotation.table.Table;
import com.lucky.jacklamb.annotation.table.Tables;
import com.lucky.jacklamb.datasource.LuckyDataSource;
import com.lucky.jacklamb.datasource.LuckyDataSourceManage;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlCoreFactory;
import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/18 下午11:17
 */
public class LuckyDataJacklambBeanFactory extends IOCBeanFactory {

    private static final Logger log= LoggerFactory.getLogger("c.l.d.b.LuckyDataJacklambBeanFactory");

    @Override
    public List<Module> createBean() {
        List<Module> mappers=new ArrayList<>();
        List<Class<?>> mapperClasses = getPluginByAnnotation(Mapper.class);
        //注册数据源
        getBeanByClass(LuckyDataSource.class)
                .stream()
                .map(m->(LuckyDataSource)m.getComponent())
                .forEach(LuckyDataSourceManage::addLuckyDataSource);

        //创建SqlCore对象
        List<LuckyDataSource> allDataSource = LuckyDataSourceManage.getAllDataSource();
        Set<String> createTables = getClasses(Table.class, Tables.class).stream().map(c->c.getName()).collect(Collectors.toSet());
        for (LuckyDataSource luckyDataSource : allDataSource) {
            luckyDataSource.setCreateTable(createTables);
            String dbname = luckyDataSource.getDbname();
            Module sqlCore = new Module("SqlCore-" + dbname, "SqlCore", SqlCoreFactory.createSqlCore(dbname));
            mappers.add(sqlCore);
            log.info("Create SqlCore `{}`",sqlCore);

        }
        //创建Mapper的代理
        for (Class<?> mapperClass : mapperClasses) {
            String dbname=AnnotationUtils.get(mapperClass,Mapper.class).dbname();
            Module mapper = new Module(getBeanName(mapperClass)
                    , getBeanType(mapperClass),
                    SqlCoreFactory.createSqlCore(dbname).getMapper(mapperClass));
            mappers.add(mapper);
            log.info("Create Mapper `{}`",mapper);
        }
        return mappers;
    }

    public String getBeanName(Class<?> aClass) {
        Mapper mapper = AnnotationUtils.get(aClass, Mapper.class);
        String id=mapper.id();
        return Assert.isBlankString(id)? Namer.getBeanName(aClass):id;
    }

    @Override
    public String getBeanType(Class<?> aClass) {
        return "mapper";
    }
}
