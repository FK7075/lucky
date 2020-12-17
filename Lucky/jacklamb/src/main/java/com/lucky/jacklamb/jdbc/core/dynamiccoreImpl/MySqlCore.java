package com.lucky.jacklamb.jdbc.core.dynamiccoreImpl;

import com.lucky.jacklamb.createtable.CreateTableSqlGenerate;
import com.lucky.jacklamb.createtable.MySqlCreateTableSqlGenerate;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlCore;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlGroup;
import com.lucky.jacklamb.jdbc.core.sql.BatchInsert;
import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.jacklamb.querybuilder.ObjectToJoinSql;
import com.lucky.jacklamb.querybuilder.QueryBuilder;
import com.lucky.jacklamb.reverse.TableToJava;
import com.lucky.utils.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public final class MySqlCore extends SqlCore {

	private TableToJava tableToJava;
	
	public MySqlCore(String dbname) {
		super(dbname);
		tableToJava=new TableToJava(dbname);
	}

	@Override
	public CreateTableSqlGenerate getCreateTableSqlGenerate() {
		return new MySqlCreateTableSqlGenerate();
	}

	@Override
	public SqlGroup getSqlGroup() {
		return new MySqlGroup();
	}

	@Override
	public void createJavaBean() {
		tableToJava.generateJavaSrc();
	}

	@Override
	public void createJavaBean(String srcPath) {
		tableToJava.generateJavaSrc(srcPath);
	}

	@Override
	public void createJavaBeanByTable(String... tables) {
		tableToJava.b_generateJavaSrc(tables);
	}

	@Override
	public void createJavaBeanSrc(String srcPath, String... tables) {
		tableToJava.a_generateJavaSrc(srcPath, tables);
		
	}


	@Override
	public <T> List<T> query(QueryBuilder queryBuilder, Class<T> resultClass, String... expression) {
		queryBuilder.setDbname(getDbName());
		queryBuilder.setWheresql(new MySqlGroup());
		ObjectToJoinSql join = new ObjectToJoinSql(queryBuilder);
		String sql = join.getJoinSql(expression);
		Object[] obj = join.getJoinObject();
		return getList(resultClass, sql, obj);
	}

	@Override
	public <T> int insertByCollection(Collection<T> collection) {
		if(collection.isEmpty())
			return -1;
		setUUID(collection);
		BatchInsert bbi=new BatchInsert(collection,dbname);
		return statementCore.update(bbi.getInsertSql(), bbi.getInsertObject());
	}

	/**
	 * 设置自增主键
	 * @param pojo
	 */
	@Override
	public void setNextId(Object pojo) {
		Class<?> pojoClass=pojo.getClass();
		String sql="SELECT auto_increment FROM information_schema.`TABLES` WHERE TABLE_SCHEMA=? AND table_name=?";
		int nextid= statementCore.getObject(int.class, sql, PojoManage.getDatabaseName(dbname),PojoManage.getTable(pojoClass,getDbName()))-1;
		Field idf= PojoManage.getIdField(pojoClass);
		FieldUtils.setValue(pojo,idf,nextid);
	}
}

class MySqlGroup extends SqlGroup{

	@Override
	public String sqlGroup(String res, String onsql, String andsql, String like, String sort) {
		if(!andsql.contains("WHERE")&&!"".equals(like)) {
			like=" WHERE "+like;
		}
		if(page==null&&rows==null) {
			return "SELECT "+res+" FROM " + onsql + andsql+like+sort;
		}else {
			return "SELECT "+res+" FROM " + onsql + andsql+like+sort+" LIMIT "+(page-1)*rows+","+rows;
		}
	}
	
}

