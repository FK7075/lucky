package com.lucky.jacklamb.jdbc.core;

import com.lucky.jacklamb.jdbc.transaction.JDBCTransaction;
import com.lucky.jacklamb.jdbc.transaction.Transaction;

import java.lang.reflect.Method;
import java.util.List;

public class TransactionSqlActuator extends SqlActuator {

    private Transaction tr;

    public Transaction openTransaction() {
        tr.open();
        return tr;
    }

    @Override
    public Transaction openTransaction(int isolationLevel) {
        tr.open(isolationLevel);
        return tr;
    }

    public TransactionSqlActuator(String dbname){
        super(dbname);
        tr= new JDBCTransaction(dataSource.getConnection());
    }

    @Override
    public <T> List<T> autoPackageToList(Class<T> c, String sql, Object... obj) {
        SqlAndParams sp=new SqlAndParams(sql,obj);
        SqlOperation sqlOperation=new SqlOperation(tr.getConnection(),dbname,isFullMap);
        List<T> result = sqlOperation.autoPackageToList(c, sp.precompileSql, sp.params);
        return result;
    }

    @Override
    public int update(String sql, Object... obj) {
        SqlAndParams sp=new SqlAndParams(sql,obj);
        SqlOperation sqlOperation=new SqlOperation(tr.getConnection(),dbname,isFullMap);
        int result = sqlOperation.setSql(sp.precompileSql, sp.params);
        return result;
    }

    @Override
    public <T> List<T> autoPackageToListMethod(Class<T> c, Method method, String sql, Object[] obj) {
        SqlAndParams sp=new SqlAndParams(method,sql,obj);
        SqlOperation sqlOperation=new SqlOperation(tr.getConnection(),dbname,isFullMap);
        List<T> result = sqlOperation.autoPackageToList(c, sp.precompileSql, sp.params);
        return result;
    }

    @Override
    public int updateMethod(Method method, String sql, Object[] obj) {
        SqlAndParams sp=new SqlAndParams(method,sql,obj);
        SqlOperation sqlOperation=new SqlOperation(tr.getConnection(),dbname,isFullMap);
        int result = sqlOperation.setSql(sp.precompileSql, sp.params);
        return result;
    }

    @Override
    public int[] updateBatch(String sql, Object[][] obj) {
        SqlOperation sqlOperation=new SqlOperation(tr.getConnection(),dbname,isFullMap);
        int[] result = sqlOperation.setSqlBatch(sql, obj);
        return result;
    }

    @Override
    public int[] updateBatch(String... completeSqls) {
        if(completeSqls.length!=0){
            SqlOperation sqlOperation=new SqlOperation(tr.getConnection(),dbname,isFullMap);
            int[] result = sqlOperation.setSqlBatch(completeSqls);
            return result;
        }
        return new int[0];
    }
}
