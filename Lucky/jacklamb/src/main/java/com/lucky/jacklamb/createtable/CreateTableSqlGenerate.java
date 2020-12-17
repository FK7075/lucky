package com.lucky.jacklamb.createtable;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

/**
 * 创建数据库表SQL的生成器
 * @author fk7075
 * @version 1.0
 * @date 2020/10/23 11:50
 */
public interface CreateTableSqlGenerate {

    /**
     * 根据一个实体的Class以及dbname得到对应数据库表的建表语句
     * @param dbname 数据源
     * @param pojoClass 实体类的Class对象
     * @return 建表SQL语句
     */
    String createTableSql(String dbname, Class<?> pojoClass);

    /**
     * 得到一个实体对应表的索引、外键的删除语句
     * @param queryCreateSQL 原表的建表语句
     * @param tableName 表名
     * @return 删除索引和外键的SQL语句
     */
    List<String> deleteKeyAndIndexSQL(String queryCreateSQL, String tableName);

    /**
     * 得到一个实体对应表的索引、外键的添加语句
     * @param dbname 数据源
     * @param pojoClass 实体类的Class对象
     * @return 添加索引和外键的SQL语句
     */
    List<String> addKeyAndIndexSQL(String dbname, Class<?> pojoClass);

    /**
     * 得到表的DDL信息
     * @param conn
     * @param tableName
     * @return
     */
    String getDDL(Connection conn, String tableName);

    /**
     * 生成外键名
     * @return
     */
    default String getRandomStr() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}
