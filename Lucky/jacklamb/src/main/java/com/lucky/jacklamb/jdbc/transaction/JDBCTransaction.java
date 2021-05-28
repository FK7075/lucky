package com.lucky.jacklamb.jdbc.transaction;

import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.jacklamb.exception.LuckyTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCTransaction implements Transaction {

    private static final Logger log= LoggerFactory.getLogger("c.l.j.a.transaction.JDBCTransaction");

    private final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    public JDBCTransaction(Connection connection) {
        this.connectionThreadLocal.set(connection);
    }

    @Override
    public Connection getConnection() {
        return connectionThreadLocal.get();
    }

    @Override
    public void open() {
        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            log.error("开启事务失败！",e);
            throw new LuckyTransactionException("开启事务失败！",e);
        }
    }

    @Override
    public void setIsolation(int isolation) {
        try {
            getConnection().setTransactionIsolation(isolation);
        } catch (SQLException e) {
            log.error("设置隔离级别失败！[(ERROR)TRANSACTION_ISOLATION : "+isolation+"]",e);
            throw new LuckyTransactionException("设置隔离级别失败！",e);
        }
    }

    @Override
    public void open(int isolationLevel) {
        setIsolation(isolationLevel);
        open();

    }

    @Override
    public void commit() {
        try {
            getConnection().commit();
            close();
        } catch (SQLException e) {
            log.error("提交事务失败！",e);
            throw new LuckyTransactionException("提交事务失败！",e);
        }
    }

    @Override
    public void rollback() {
        try {
            getConnection().rollback();
            close();
        } catch (SQLException e) {
            log.error("事务回滚失败！",e);
            throw new LuckyTransactionException("事务回滚失败！",e);
        }
    }

    @Override
    public void close() {
        LuckyDataSource.close(null,null,getConnection());
    }
}
