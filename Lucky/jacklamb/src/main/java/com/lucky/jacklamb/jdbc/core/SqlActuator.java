package com.lucky.jacklamb.jdbc.core;

import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.datasource.sql.LuckyDataSourceManage;
import com.lucky.jacklamb.jdbc.core.SqlOperation;
import com.lucky.jacklamb.jdbc.transaction.Transaction;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

/**
 * SQL执行器，用来执行一条SQL语句，并且将返回的结果自动封装为对应的对象
 */
public abstract class SqlActuator {

    protected LuckyDataSource dataSource;

    protected String dbname;

    protected boolean isFullMap;

    public boolean isFullMap() {
        return isFullMap;
    }

    public void setFullMap(boolean fullMap) {
        isFullMap = fullMap;
    }

    public SqlActuator(String dbname) {
        this.dbname=dbname;
        this.dataSource= LuckyDataSourceManage.getDataSource(dbname);
//        //初始化数据源
//        dataSource.init();
    }

    /**
     * 自动将查询结果集中的内容封装起来
     * @param c 封装类的Class对象
     * @param sql 预编译的sql语句
     * @param obj 替换占位符的数组
     * @return 返回一个泛型的List集合
     */
    public abstract <T> List<T> autoPackageToList(Class<T> c, String sql, Object... obj);

    /**
     * 执行一个非查询语句，返回此次操作影响的行数
     * @param sql 预编译的sql语句
     * @param obj 替换占位符的数组
     * @return 受影响的行数
     */
    public abstract int update(String sql, Object...obj);

    /**
     *  自动将查询结果集中的内容封装起来
     * @param c 封装类的Class对象
     * @param method findBy语法方法
     * @param sql 预编译的sql语句
     * @param obj 替换占位符的数组
     * @param <T>
     * @return
     */
    public abstract <T> List<T>  autoPackageToListMethod(Class<T> c, Method method, String sql, Object[] obj);

    /**
     * 执行一个非查询语句，返回此次操作影响的行数
     * @param method findBy语法方法
     * @param sql 预编译的sql语句
     * @param obj 替换占位符的数组
     * @return
     */
    public abstract int updateMethod(Method method, String sql, Object[]obj);

    /**
     * 基于PreparedStatement的批量操作
     * @param sql 预编译SQL
     * @param obj 替换占位符的数组
     * @return
     */
    public abstract int[] updateBatch(String sql,Object[][] obj);

    /**
     * 基于Statement的批量操作
     * @param completeSqls 完整的SQL语句集合
     * @return
     */
    public abstract int[] updateBatch(String...completeSqls);

    /**
     * 开启事务
     * @return
     */
    public abstract Transaction openTransaction();

    /**
     * 开启缓存，并且设置隔离级别
     * @param isolationLevel
     * @return
     */
    public abstract Transaction openTransaction(int isolationLevel);

    /**
     * 清空缓存
     */
    public void clear(){
        SqlOperation.resultCache.get(dbname).clear();
    }

    /**
     * 返回一个数据库连接
     * @return
     */
    public Connection getConnection(){
        return dataSource.getConnection();
    }
}
