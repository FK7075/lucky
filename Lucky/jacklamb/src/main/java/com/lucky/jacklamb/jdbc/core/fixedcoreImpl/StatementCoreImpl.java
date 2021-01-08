package com.lucky.jacklamb.jdbc.core.fixedcoreImpl;

import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.jacklamb.jdbc.core.DefaultSqlActuator;
import com.lucky.jacklamb.jdbc.core.SqlActuator;
import com.lucky.jacklamb.jdbc.core.TransactionSqlActuator;
import com.lucky.jacklamb.jdbc.core.abstcore.StatementCore;
import com.lucky.jacklamb.jdbc.transaction.Transaction;

import java.lang.reflect.Method;
import java.util.List;

@SuppressWarnings("unchecked")
public final class StatementCoreImpl implements StatementCore {
	
	private String dbname;

	private SqlActuator sqlActuator;

	public void setFullMap(boolean isFullMap){
		sqlActuator.setFullMap(isFullMap);
	}

	public Transaction openTransaction(){
		return sqlActuator.openTransaction();
	}

	public Transaction openTransaction(int isolationLevel){
		return sqlActuator.openTransaction(isolationLevel);
	}

	private StatementCoreImpl(LuckyDataSource dataSource){
		this.dbname=dataSource.getDbname();
	}

	public static StatementCoreImpl getDefaultStatementCoreImpl(LuckyDataSource dataSource){
		StatementCoreImpl sc=new StatementCoreImpl(dataSource);
		sc.sqlActuator=new DefaultSqlActuator(sc.dbname);
		return sc;
	}

	public static StatementCoreImpl getTransactionStatementCoreImpl(LuckyDataSource dataSource){
		StatementCoreImpl sc=new StatementCoreImpl(dataSource);
		sc.sqlActuator=new TransactionSqlActuator(sc.dbname);
		return sc;
	}

	
	
	@Override
	public <T> List<T> getList(Class<T> c, String sql, Object... obj) {
		return this.sqlActuator.autoPackageToList(c, sql, obj);
	}

	@Override
	public <T> List<T> getListMethod(Class<T> c,Method method, String sql, Object[] obj) {
		return this.sqlActuator.autoPackageToListMethod(c,method, sql, obj);
	}

	@Override
	public <T> T getObject(Class<T> c, String sql, Object... obj) {
		List<T> list = getList(c,sql,obj);
		if(list==null||!list.isEmpty())
			return list.get(0);
		return null;
	}

	@Override
	public <T> T getObjectMethod(Class<T> c,Method method, String sql, Object[] obj) {
		List<T> list = getListMethod(c,method,sql,obj);
		if(list==null||!list.isEmpty())
			return list.get(0);
		return null;
	}

	@Override
	public int update(String sql, Object... obj) {
		return this.sqlActuator.update(sql, obj);
	}

	@Override
	public int updateMethod(Method method, String sql, Object[] obj) {
		return this.sqlActuator.updateMethod(method,sql, obj);
	}

	@Override
	public int[] updateBatch(String sql, Object[][] obj) {
		return this.sqlActuator.updateBatch(sql, obj);
	}

	@Override
	public int[] updateBatch(String... completeSqls) {
		return sqlActuator.updateBatch(completeSqls);
	}


	@Override
	public void clear() {
		sqlActuator.clear();
	}

}
