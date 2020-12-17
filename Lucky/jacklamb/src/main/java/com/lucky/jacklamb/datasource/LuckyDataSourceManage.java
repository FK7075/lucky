package com.lucky.jacklamb.datasource;

import com.lucky.jacklamb.exception.NotFoundDataSourceException;
import com.lucky.utils.base.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
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

    public static Map<String, LuckyDataSource> dbMap=new HashMap<>();

    public static LuckyDataSource getDataSource(String dbname){
        LuckyDataSource luckyDataSource = dbMap.get(dbname);
        Assert.notNull(luckyDataSource,"没有找到与`"+dbname+"`相关的数据源！");
        return luckyDataSource;
    }

    public static List<LuckyDataSource> getAllDataSource(){
        return (List<LuckyDataSource>) dbMap.values();
    }


}
