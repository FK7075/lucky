package com.lucky.jacklamb.createtable;

import com.lucky.datasource.sql.LuckyDataSourceManage;
import com.lucky.jacklamb.jdbc.core.DefaultSqlActuator;
import com.lucky.jacklamb.jdbc.potable.PojoManage;

import java.util.*;

/**
 * 建表SQL语句执行器
 * @author fk7075
 * @version 1.0
 * @date 2020/10/23 13:01
 */
public class CreateTableSqlExecute {
    private DefaultSqlActuator autoPackage;
    private Set<Class<?>> classlist;
    private String dbname;

    public CreateTableSqlExecute(String dbname) {
        this.dbname = dbname;
        classlist = LuckyDataSourceManage.getDataSource(dbname).getCreateTable();
        autoPackage = new DefaultSqlActuator(dbname);
    }

    static final String queryTemp="SHOW CREATE TABLE %s";

    /**
     * 执行建表语句
     * @param generate SQL生成器
     */
    public void executeCreateTableSql(CreateTableSqlGenerate generate){
        createTable(classlist,generate);
        delAndAddIndexKey(classlist,generate);
    }

    /**
     * 执行建表语句
     * @param generate SQL生成器
     * @param pojoClasses 实体Class
     */
    public void executeCreateTableSql(CreateTableSqlGenerate generate,Class<?>...pojoClasses){
        Set<Class<?>> pojoClassList= new HashSet<>(Arrays.asList(pojoClasses));
        createTable(pojoClassList,generate);
        delAndAddIndexKey(pojoClassList,generate);
    }

    /**
     * 执行建表
     * @param pojoClasses
     * @param generate
     */
    private void createTable(Set<Class<?>> pojoClasses,CreateTableSqlGenerate generate){
        List<String> createTableSqlList=new ArrayList<>();
        pojoClasses.stream().forEach((clzz)->{
            createTableSqlList.add(generate.createTableSql(dbname,clzz));
        });
        autoPackage.updateBatch(createTableSqlList.toArray(new String[createTableSqlList.size()]));
    }

    /**
     * 添加外键和索引
     * @param pojoClasses
     * @param generate
     */
    private void delAndAddIndexKey(Set<Class<?>> pojoClasses,CreateTableSqlGenerate generate){
        List<String> deleteKeyAndIndexSqlList=new ArrayList<>();
        List<String> addKeyAndIndexSqlList=new ArrayList<>();
        pojoClasses.stream().forEach((clzz)->{
            String tableName= PojoManage.getTable(clzz,dbname);
             deleteKeyAndIndexSqlList.addAll(generate.deleteKeyAndIndexSQL(generate.getDDL(autoPackage.getConnection(),tableName),tableName));
            addKeyAndIndexSqlList.addAll(generate.addKeyAndIndexSQL(dbname,clzz));
        });
        deleteKeyAndIndexSqlList.addAll(addKeyAndIndexSqlList);
        autoPackage.updateBatch(deleteKeyAndIndexSqlList.toArray(new String[deleteKeyAndIndexSqlList.size()]));
    }

}

