package com.lucky.jacklamb.jdbc.core.dynamiccoreImpl;

import com.lucky.jacklamb.createtable.CreateTableSqlGenerate;
import com.lucky.jacklamb.createtable.PostgreSqlCreateTableSqlGenerate;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlCore;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlGroup;
import com.lucky.jacklamb.jdbc.core.sql.BatchInsert;
import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.jacklamb.querybuilder.ObjectToJoinSql;
import com.lucky.jacklamb.querybuilder.QueryBuilder;
import com.lucky.utils.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public final class PostgreSqlCore extends SqlCore {

	public PostgreSqlCore(String dbname) {
		super(dbname);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SqlGroup getSqlGroup() {
		return new PostgreSqlGroup();
	}

	@Override
	public void createJavaBean() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createJavaBean(String srcPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createJavaBeanByTable(String... tables) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createJavaBeanSrc(String srcPath, String... tables) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CreateTableSqlGenerate getCreateTableSqlGenerate() {
		return new PostgreSqlCreateTableSqlGenerate();
	}


	@Override
	public <T> List<T> query(QueryBuilder queryBuilder, Class<T> resultClass, String... expression) {
		queryBuilder.setDbname(getDbName());
		queryBuilder.setWheresql(new PostgreSqlGroup());
		ObjectToJoinSql join = new ObjectToJoinSql(queryBuilder);
		String sql = join.getJoinSql(expression);
		Object[] obj = join.getJoinObject();
		return getList(resultClass, sql, obj);
	}

	@Override
	public <T> int insertByCollection(Collection<T> collection) {
		setUUID(collection);
		BatchInsert bbi=new BatchInsert(collection,dbname);
		return statementCore.update(bbi.getInsertSql(), bbi.getInsertObject());
	}

	@Override
	public void setNextId(Object pojo) {
		Class<?> pojoClass=pojo.getClass();
		String sql="SELECT last_value FROM "+PojoManage.getTable(pojoClass,dbname)+"_"+PojoManage.getIdString(pojoClass,dbname)+"_seq";
		int nextid= statementCore.getObject(int.class, sql);
		Field idf= PojoManage.getIdField(pojoClass);
		FieldUtils.setValue(pojo,idf,nextid);
	}

}

class PostgreSqlGroup extends SqlGroup{

	@Override
	public String sqlGroup(String res, String onsql, String andsql, String like, String sort) {
		if(!andsql.contains("WHERE")&&!"".equals(like)) {
			like=" WHERE "+like;
		}
		if(page==null&&rows==null) {
			return "SELECT "+res+" FROM " + onsql + andsql+like+sort;
		}else {
			return "SELECT "+res+" FROM " + onsql + andsql+like+sort+" LIMIT "+rows+" OFFSET "+(page-1)*rows;
		}
	}
	
}