package com.lucky.jacklamb.jdbc.transaction;

import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.jacklamb.exception.LuckyTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCTransaction implements Transaction {

    private static final Logger log= LoggerFactory.getLogger("c.l.j.a.transaction.JDBCTransaction");

    private Connection connection;

    public JDBCTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void open() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            log.error("开启事务失败！",e);
            throw new LuckyTransactionException("开启事务失败！",e);
        }
    }

    @Override
    public void open(int isolationLevel) {
        try {
            connection.setTransactionIsolation(isolationLevel);
            open();
        } catch (SQLException e) {
            log.error("设置隔离级别失败！[(ERROR)TRANSACTION_ISOLATION : "+isolationLevel+"]",e);
            throw new LuckyTransactionException("设置隔离级别失败！",e);
        }
    }

    @Override
    public void commit() {
        try {
            connection.commit();
            close();
        } catch (SQLException e) {
            log.error("提交事务失败！",e);
            throw new LuckyTransactionException("提交事务失败！",e);
        }
    }

    @Override
    public void rollback() {
        try {
            connection.rollback();
            close();
        } catch (SQLException e) {
            log.error("事务回滚失败！",e);
            throw new LuckyTransactionException("事务回滚失败！",e);
        }
    }

    @Override
    public void close() {
        LuckyDataSource.close(null,null,connection);
    }
}
