package com.lucky.jacklamb.jdbc.core.dynamiccoreImpl;

import com.lucky.jacklamb.createtable.CreateTableSqlGenerate;
import com.lucky.jacklamb.createtable.OracleCreateTableSqlGenerate;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlCore;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlGroup;
import com.lucky.jacklamb.jdbc.core.sql.BatchInsert;
import com.lucky.jacklamb.querybuilder.ObjectToJoinSql;
import com.lucky.jacklamb.querybuilder.QueryBuilder;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public final class OracleCore extends SqlCore {

	public OracleCore(String dbname) {
		super(dbname);
	}

	@Override
	public SqlGroup getSqlGroup() {
		return new OracleSqlGroup();
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
		return new OracleCreateTableSqlGenerate();
	}

	@Override
	public <T> List<T> query(QueryBuilder queryBuilder, Class<T> resultClass, String... expression) {
		queryBuilder.setDbname(getDbName());
		queryBuilder.setWheresql(new OracleSqlGroup());
		ObjectToJoinSql join = new ObjectToJoinSql(queryBuilder);
		String sql = join.getJoinSql(expression);
		Object[] obj = join.getJoinObject();
		return getList(resultClass, sql, obj);
	}

	@Override
	public <T> int insertByCollection(Collection<T> collection) {
		setUUID(collection);
		BatchInsert bbi=new BatchInsert(collection,dbname);
		return statementCore.update(bbi.OrcaleInsetSql(), bbi.getInsertObject());
	}

	@Override
	public void setNextId(Object pojo) {
		// TODO Auto-generated method stub
		
	}


}

class OracleSqlGroup extends SqlGroup{

	@Override
	public String sqlGroup(String res, String onsql, String andsql, String like, String sort) {
		if(!andsql.contains("WHERE")&&!"".equals(like)) {
			like=" WHERE "+like;
		}
		if(page==null&&rows==null) {
			return "SELECT "+res+" FROM " + onsql + andsql+like+sort;
		}else {
			int start=(page-1)*rows;
			int end=start+rows-1;
			return " SELECT * FROM (SELECT lucy.*,ROWNUM jack FROM (SELECT "+res+" FROM " + onsql + andsql+like+sort+") lucy WHERE ROWNUM<="+end+") WHERE jack>="+start;
		}
	}
	
}
