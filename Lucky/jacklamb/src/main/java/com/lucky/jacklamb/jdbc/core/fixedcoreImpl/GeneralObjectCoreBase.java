package com.lucky.jacklamb.jdbc.core.fixedcoreImpl;

import com.lucky.jacklamb.createtable.CreateTableSqlExecute;
import com.lucky.jacklamb.datasource.LuckyDataSource;
import com.lucky.jacklamb.datasource.LuckyDataSourceManage;
import com.lucky.jacklamb.enums.PrimaryType;
import com.lucky.jacklamb.jdbc.core.abstcore.GeneralObjectCore;
import com.lucky.jacklamb.jdbc.core.abstcore.UniqueSqlCore;
import com.lucky.jacklamb.jdbc.core.sql.CreateSql;
import com.lucky.jacklamb.jdbc.core.sql.GeneralSqlGenerator;
import com.lucky.jacklamb.jdbc.core.sql.PrecompileSqlAndObject;
import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.jacklamb.jdbc.transaction.Transaction;
import com.lucky.utils.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("unchecked")
public abstract class GeneralObjectCoreBase implements GeneralObjectCore, UniqueSqlCore {
	
	private GeneralSqlGenerator gcg;
	
	protected StatementCoreImpl statementCore;

	public void setStatementCore(StatementCoreImpl statementCore) {
		this.statementCore = statementCore;
	}

	private LuckyDataSource dataSource;

	public LuckyDataSource getDataSource() {
		return dataSource;
	}

	protected CreateTableSqlExecute createTableSqlExecute;

	protected String dbname;


	public GeneralObjectCoreBase(String dbname){
		this.dbname=dbname;
		gcg=new GeneralSqlGenerator(dbname);
		this.dataSource= LuckyDataSourceManage.getDataSource(dbname);
		this.createTableSqlExecute=new CreateTableSqlExecute(dbname);
	}


	protected Transaction openTransaction(){
		return statementCore.openTransaction();
	}

	protected Transaction openTransaction(int isolationLevel){
		return statementCore.openTransaction(isolationLevel);
	}

	@Override
	public <T> T getOne(Class<T> c, Object id) {
		String ysql = gcg.getOneSql(c);
		return statementCore.getObject(c, ysql, id);
	}

	@Override
	public <T> T getObject(T t) {
		PrecompileSqlAndObject select = gcg.singleSelect(t);
		String ysql = select.getPrecompileSql();
		Object[] objects=select.getObjects().toArray();
		return (T) statementCore.getObject(t.getClass(), ysql, objects);
	}

	@Override
	public <T> List<T> getList(T t) {
		PrecompileSqlAndObject select = gcg.singleSelect(t);
		String ysql = select.getPrecompileSql();
		Object[] objects=select.getObjects().toArray();
		return (List<T>) statementCore.getList(t.getClass(), ysql, objects);
	}

	@Override
	public <T> int count(T t) {
		PrecompileSqlAndObject select = gcg.singleCount(t);
		String ysql = select.getPrecompileSql();
		Object[] objects=select.getObjects().toArray();
		return statementCore.getObject(int.class, ysql, objects);
	}

	@Override
	public <T> int delete(T t) {
		PrecompileSqlAndObject delete = gcg.singleDelete(t);
		return statementCore.update(delete.getPrecompileSql(), delete.getObjects().toArray());
	}

	@Override
	public <T> int updateRow(T t, String...conditions) {
		PrecompileSqlAndObject update = gcg.singleUpdate(t,conditions);
		return statementCore.update(update.getPrecompileSql(), update.getObjects().toArray());
	}

	@Override
	public int deleteByArray(Object... obj) {
		List<Object> objects = Arrays.asList(obj);
		return deleteByCollection(objects);
	}

	@Override
	public <T> int deleteByCollection(Collection<T> collection) {
		PrecompileSqlAndObject delete;
		List<String> completeSqls=new ArrayList<>();
		for (T t : collection) {
			delete = gcg.singleDelete(t);
			completeSqls.add(CreateSql.getCompleteSql(delete.getPrecompileSql(),delete.getObjects().toArray()));
		}
		String[] sqls=new String[completeSqls.size()];
		completeSqls.toArray(sqls);
		return getResult(statementCore.updateBatch(sqls));
	}

	@Override
	public int updateByArray(Object... obj) {
		List<Object> objects = Arrays.asList(obj);
		return updateByCollection(objects);
	}

	@Override
	public <T> int updateByCollection(Collection<T> collection) {
		PrecompileSqlAndObject update;
		List<String> completeSqls=new ArrayList<>();
		for (T t : collection) {
			update = gcg.singleUpdate(t);
			completeSqls.add(CreateSql.getCompleteSql(update.getPrecompileSql(),update.getObjects().toArray()));
		}
		String[] sqls=new String[completeSqls.size()];
		completeSqls.toArray(sqls);
		return getResult(statementCore.updateBatch(sqls));
	}

	@Override
	public int delete(Class<?> clazz, Object id) {
		String ysql = gcg.deleteOneSql(clazz);
		return statementCore.update(ysql, id);
	}

	@Override
	public int deleteByIdIn(Class<?> clazz, Object[] ids) {
		String ysql =gcg.deleteIn(clazz, ids);
		return statementCore.update(ysql, ids);
	}

	@Override
	public <T> List<T> getByIdIn(Class<T> clazz, Object[] ids) {
		String ysql =gcg.selectIn(clazz, ids);
		return statementCore.getList(clazz,ysql, ids);
	}

	@Override
	public <T> int insert(T pojo) {
		PrecompileSqlAndObject insert=gcg.singleInsert(pojo);
		return statementCore.update(insert.getPrecompileSql(), insert.getObjects().toArray());
	}

	@Override
	public <T> int insertSetIdByArray(Object... obj) {
		return insertByCollection(Arrays.asList(obj));
	}

	@Override
	public <T> int insertByCollection(Collection<T> collection) {
		PrecompileSqlAndObject insert;
		List<String> completeSqls=new ArrayList<>();
		for (T t : collection) {
			insert=gcg.singleInsert(t);
			completeSqls.add(CreateSql.getCompleteSql(insert.getPrecompileSql(),insert.getObjects().toArray()));
		}
		setUUID(completeSqls);
		String[] sqls=new String[completeSqls.size()];
		completeSqls.toArray(sqls);
		return getResult(statementCore.updateBatch(sqls));
	}

	protected <T> void setUUID(Collection<T> collection){
		Class<?> pClass=null;
		boolean isUUID=false;
		Field idField=null;
		for (T t : collection) {
			if (pClass == null) {
				pClass = t.getClass();
				idField= PojoManage.getIdField(pClass);
				isUUID=PojoManage.getIdType(pClass,dbname)== PrimaryType.AUTO_UUID;
			}
			if (isUUID) {
				FieldUtils.setValue(t, idField, UUID.randomUUID().toString());
			}
		}
	}

	protected int getResult(int[] arr){
		int s=0;
		for (int i : arr) {
			s+=i;
		}
		return s;
	}
}
