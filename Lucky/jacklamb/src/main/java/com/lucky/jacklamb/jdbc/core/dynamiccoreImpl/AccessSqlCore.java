package com.lucky.jacklamb.jdbc.core.dynamiccoreImpl;

import com.lucky.jacklamb.createtable.CreateTableSqlGenerate;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlCore;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlGroup;
import com.lucky.jacklamb.querybuilder.QueryBuilder;

import java.util.Collection;
import java.util.List;

public final class AccessSqlCore extends SqlCore {

	public AccessSqlCore(String dbname) {
		super(dbname);
	}

	@Override
	public SqlGroup getSqlGroup() {
		return null;
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
		return null;
	}

	@Override
	public <T> List<T> query(QueryBuilder queryBuilder, Class<T> resultClass, String... expression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> int insertByCollection(Collection<T> collection) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setNextId(Object pojo) {
		// TODO Auto-generated method stub
		
	}

}
