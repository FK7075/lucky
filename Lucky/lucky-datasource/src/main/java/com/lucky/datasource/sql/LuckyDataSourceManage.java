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

    private LuckyDataSourceManage(){}
    private static boolean isFirst=true;
    private static String DEFAULT_POOL_TYPE_NAME;
    private static Map<String, LuckyDataSource> dbMap=new HashMap<>();
    private static Map<String,Class<? extends LuckyDataSource>> poolTypeMap=new HashMap<>();

    /**
     * 获取一个数据源对象
     * @param dbname 数据源的唯一ID
     * @return
     */
    public static LuckyDataSource getDataSource(String dbname){
        init();
        LuckyDataSource luckyDataSource = dbMap.get(dbname);
        Assert.notNull(luckyDataSource,"没有找到dbname=`"+dbname+"`的数据源！");
        return luckyDataSource;
    }

    /**
     * 得到所有的数据源
     * @return
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
     * 注册一个新的连接池
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
     * 手动添加一个数据源
     * @param luckyDataSource 数据源
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

    public static void confLuckyDataSource(){
        Map<String, Object> map = ConfigUtils.getYamlConfAnalysis().getMap();
        if(!map.containsKey("lucky")){
            return;
        }
        Map<String, Object> lucky = (Map<String, Object>) map.get("lucky");
        if(!lucky.containsKey("datasource")){
            return;
        }
        Map<String, Object> jdbc = (Map<String, Object>) lucky.get("datasource");
        for(Map.Entry<String,Object> datasource:jdbc.entrySet()){
            Map<String,Object> dataInfo= (Map<String, Object>) datasource.getValue();
            LuckyDataSource luckyDataSource = createLuckyDataSourceByConf(datasource.getKey(),dataInfo);
            luckyDataSource.setDbname(datasource.getKey());
            addLuckyDataSource(luckyDataSource);
        }

    }


    //使用配置文件创建一个LuckyDataSource
    private static LuckyDataSource createLuckyDataSourceByConf(String dbname,Map<String,Object> confMap){
        Object pt = confMap.get("pool-type");
        String poolType;
        if(pt==null){
            Assert.notNull(DEFAULT_POOL_TYPE_NAME,
                    "从配置文件初始化dbname=`"+dbname+"`数据源时失败！由于找不到连接池配置项`pool-type`,尝试使用默认的连接池初始化，尝试失败！（未设置默认的连接池）DEFAULT_POOL_TYPE_NAME=NULL");
            poolType=DEFAULT_POOL_TYPE_NAME;
        }else{
            poolType=pt.toString();
        }

        if(!poolTypeMap.containsKey(poolType)){
            throw new RuntimeException("从配置文件初始化dbname=`"+dbname+"`数据源时失败！数据源解析错误：poolType为`"+poolType+"`的连接池没有注册！");
        }
        LuckyDataSource luckyDataSource = ClassUtils.newObject(poolTypeMap.get(poolType));
        luckyDataSource.mapInit(confMap);
        return luckyDataSource;
    }

    private static void init(){
        if(isFirst){
            confLuckyDataSource();
            isFirst=false;
        }
    }

    public static void destroy(){
        for(Map.Entry<String, LuckyDataSource> entry: dbMap.entrySet()){
            entry.getValue().destroy();
        }
    }
}
