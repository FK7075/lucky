package com.lucky.datasource.sql;

import com.lucky.utils.annotation.NonNull;
import com.lucky.utils.base.Assert;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.reflect.ClassUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源管理器
 * @author fk
 * @version 1.0
 * @date 2020/12/17 0017 9:47
 */
public class LuckyDataSourceManage {

    /*
        数据源管理器
     */

    private LuckyDataSourceManage(){}
    private static boolean isFirst=true;
    /** 默认的连接池标识，作用于从配置文件生成数据源时 ‘pool-type’属性缺省时连接池的选取*/
    private static String DEFAULT_POOL_TYPE_NAME;
    /** 默认的数据源标识*/
    private static final String DEFAULT_DBNAME="defaultDB";
    /** 已成功注册的数据源 [dbname => DataSource] */
    private static Map<String, LuckyDataSource> dbMap=new HashMap<>();
    /** 数据源管理器支持的连接池类型 [pool-type => DataSourceType]*/
    private static Map<String,Class<? extends LuckyDataSource>> poolTypeMap=new HashMap<>();

    /**
     * 获取一个数据源对象
     * @param dbname 数据源的唯一ID
     * @return 返回与dbname对应的数据源
     */
    public static LuckyDataSource getDataSource(String dbname){
        init();
        LuckyDataSource luckyDataSource = dbMap.get(dbname);
        Assert.notNull(luckyDataSource,"没有找到dbname=`"+dbname+"`的数据源！");
        return luckyDataSource;
    }

    /**
     * 得到所有的数据源
     * @return 返回所有的数据源
     */
    public static List<LuckyDataSource> getAllDataSource(){
        init();
        List<LuckyDataSource> luckyDataSources=new ArrayList<>(dbMap.size());
        for(Map.Entry<String,LuckyDataSource> dataSourceEntry:dbMap.entrySet()){
            luckyDataSources.add(dataSourceEntry.getValue());
        }
        return luckyDataSources;
    }

    /**
     * 注册一个新的连接池类型<br/>
     * 这里注册的连接池类型将会以 pool-type ==> LuckyDataSource.class的形式
     * 存放在{@link LuckyDataSourceManage#poolTypeMap}中，当需要从配置文件解析
     * 生成数据源时管理器就会根据配置信息中的pool-type来找到对应的连接池类型，并使用
     * 该类型来实例化这个链接池<br/>
     * 注：<br/>
     * 1.数据源管理器会将第一个注册进来的连接池作为默认的连接池类型<br/>
     * 2.这个连接池类型必须为{@link LuckyDataSource}的子类
     * @param poolType 连接池的类型
     */
    public static void registerPool(@NonNull Class<? extends LuckyDataSource> poolType){
        Assert.notNull(poolType,"无法注册连接池信息: 因为poolType为NULL！");
        LuckyDataSource luckyDataSource = ClassUtils.newObject(poolType);
        if(poolTypeMap.isEmpty()){
            DEFAULT_POOL_TYPE_NAME=luckyDataSource.poolType();
        }
        poolTypeMap.put(luckyDataSource.poolType(),poolType);
    }

    /**
     * 初始化数据源管理器，解析application.yml或application.yaml中的数据源配置，
     * 并将解析生成的数据源注册到数据源管理器中
     * 注：初始化过程只会执行一次
     */
    private static void init(){
        if(isFirst){
            confLuckyDataSource();
            isFirst=false;
        }
    }


    /**
     * 注册一个数据源实例<br/>
     * 1.被注册的数据源必须有一个不为NULL的 `dbname` 标识<br/>
     * 2.`dbname`是所有数据源的唯一标识，故不可注册相同`dbname`的数据源
     *
     * @param luckyDataSource 数据源实例
     */
    public static void addLuckyDataSource(LuckyDataSource luckyDataSource){
        String dbname = luckyDataSource.getDbname();
        if(Assert.isBlankString(dbname)){
            throw new RuntimeException("数据源注册失败：不合法的dbname=`"+dbname+"`！");
        }
        if(dbMap.containsKey(dbname)){
            throw new RuntimeException("数据源注册失败：dbname=`"+dbname+"`的数据源已经存在！");
        }
        dbMap.put(dbname,luckyDataSource);
    }

    /**
     * 解析配置文件，并将配置文件中配置的所有数据源注册到数据源管理器中
     */
    public static void confLuckyDataSource(){
        Map<String, Object> map = ConfigUtils.getYamlConfAnalysis().getMap();
        if(!map.containsKey("lucky")){
            return;
        }
        Map<String, Object> lucky = (Map<String, Object>) map.get("lucky");
        if(!lucky.containsKey("datasource")){
            return;
        }
        Map<String, Object> datasources = (Map<String, Object>) lucky.get("datasource");
        for(Map.Entry<String,Object> datasource:datasources.entrySet()){
            Object datasourceValue = datasource.getValue();

            //只有一个数据源，而且这个数据源配置使用了"省略dbname"的简写方式
            if(!(datasourceValue instanceof Map)){
                LuckyDataSource luckyDataSource = createLuckyDataSourceByConf(DEFAULT_DBNAME,datasources);
                luckyDataSource.setDbname(DEFAULT_DBNAME);
                addLuckyDataSource(luckyDataSource);
                break;
            }

            //一个数据源或者多个数据源，但是都采用了“dbname”的配置方式
            Map<String,Object> dataInfo= (Map<String, Object>) datasourceValue;
            LuckyDataSource luckyDataSource = createLuckyDataSourceByConf(datasource.getKey(),dataInfo);
            luckyDataSource.setDbname(datasource.getKey());
            addLuckyDataSource(luckyDataSource);
        }
    }


    //使用配置文件创建一个LuckyDataSource
    private static LuckyDataSource createLuckyDataSourceByConf(String dbname,Map<String,Object> confMap){
        //获取数据源配置中指定要使用的连接池类型
        Object pt = confMap.get("pool-type");
        String poolType;

        //如果连接池类型配置缺失，则使用默认的连接池！
        if(pt==null){
            Assert.notNull(DEFAULT_POOL_TYPE_NAME,
                    "从配置文件初始化dbname=`"+dbname+"`数据源时失败！由于找不到连接池配置项`pool-type`,尝试使用默认的连接池初始化，尝试失败！（未设置默认的连接池）DEFAULT_POOL_TYPE_NAME=NULL");
            poolType=DEFAULT_POOL_TYPE_NAME;
        }else{
            poolType=pt.toString();
        }

        if(!poolTypeMap.containsKey(poolType)){
            //配置中指定的连接池类型在数据源管理器中没有找到
            throw new RuntimeException("从配置文件初始化dbname=`"+dbname+"`数据源时失败！数据源解析错误：poolType为`"+poolType+"`的连接池没有注册！");
        }
        //获取pool-type所对应的连接池类型，并使用反射的方式实例化
        LuckyDataSource luckyDataSource = ClassUtils.newObject(poolTypeMap.get(poolType));
        //根据配置信息初始化这个数据源
        luckyDataSource.mapInit(confMap);
        return luckyDataSource;
    }

    /**
     * 销毁所有的数据源
     */
    public static void destroy(){
        for(Map.Entry<String, LuckyDataSource> entry: dbMap.entrySet()){
            entry.getValue().destroy();
        }
    }
}
