package com.lucky.jacklamb.jdbc.core;

import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.jacklamb.exception.LuckyTransactionException;
import com.lucky.jacklamb.jdbc.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

/**
 * 结果集自动包装类
 *
 * @author fk-7075
 */

public class DefaultSqlActuator extends SqlActuator {
    private static final Logger log= LoggerFactory.getLogger("c.l.j.jdbc.core.DefaultSqlActuator");
    public DefaultSqlActuator(String dbname) {
        super(dbname);
    }

    @Override
    public <T> List<T> autoPackageToList(Class<T> c, String sql, Object... obj) {
        SqlAndParams sp = new SqlAndParams(sql, obj);
        Connection connection = dataSource.getConnection();
        SqlOperation sqlOperation = new SqlOperation(connection, dataSource.getDbname(),isFullMap);
        List<T> result = sqlOperation.autoPackageToList(c, sp.precompileSql, sp.params);
        LuckyDataSource.close(null, null, connection);
        return result;
    }

    @Override
    public int update(String sql, Object... obj) {
        SqlAndParams sp = new SqlAndParams(sql, obj);
        Connection connection = dataSource.getConnection();
        SqlOperation sqlOperation = new SqlOperation(connection, dbname,isFullMap);
        int result = sqlOperation.setSql(sp.precompileSql, sp.params);
        LuckyDataSource.close(null, null, connection);
        return result;
    }

    @Override
    public <T> List<T> autoPackageToListMethod(Class<T> c, Method method, String sql, Object[] obj) {
        SqlAndParams sp = new SqlAndParams(method, sql, obj);
        Connection connection = dataSource.getConnection();
        SqlOperation sqlOperation = new SqlOperation(connection, dbname,isFullMap);
        List<T> result = sqlOperation.autoPackageToList(c, sp.precompileSql, sp.params);
        LuckyDataSource.close(null, null, connection);
        return result;
    }

    @Override
    public int updateMethod(Method method, String sql, Object[] obj) {
        SqlAndParams sp = new SqlAndParams(method, sql, obj);
        Connection connection = dataSource.getConnection();
        SqlOperation sqlOperation = new SqlOperation(connection, dbname,isFullMap);
        int result = sqlOperation.setSql(sp.precompileSql, sp.params);
        LuckyDataSource.close(null, null, connection);
        return result;
    }

    @Override
    public int[] updateBatch(String sql, Object[][] obj) {
        Connection connection = dataSource.getConnection();
        SqlOperation sqlOperation = new SqlOperation(connection, dbname,isFullMap);
        int[] result = sqlOperation.setSqlBatch(sql, obj);
        LuckyDataSource.close(null, null, connection);
        return result;
    }

    @Override
    public int[] updateBatch(String... completeSqls) {
        if (completeSqls.length != 0) {
            Connection connection = dataSource.getConnection();
            SqlOperation sqlOperation = new SqlOperation(connection, dbname,isFullMap);
            int[] result = sqlOperation.setSqlBatch(completeSqls);
            LuckyDataSource.close(null, null, connection);
            return result;
        }
        return new int[0];
    }

    private final String ERROR = "当前使用的SQL执行器[DefaultSqlActuator]不支持事务机制，无法开启事务！若要使用事务机制请使用执行器[TransactionSqlActuator]！";

    @Override
    public Transaction openTransaction() {
        LuckyTransactionException e = new LuckyTransactionException(ERROR);
        log.error(ERROR,e);
        throw e;
    }

    @Override
    public Transaction openTransaction(int isolationLevel) {
        LuckyTransactionException e = new LuckyTransactionException(ERROR);
        log.error(ERROR,e);
        throw e;
    }

}
