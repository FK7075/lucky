package com.lucky.jacklamb.querybuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlAndObject {
	
	private String sqlStr;
	private List<Object> objects=new ArrayList<>();
	
	public String getSqlStr() {
		return sqlStr;
	}
	public void setSqlStr(String sqlStr) {
		this.sqlStr = sqlStr;
	}
	public Object[] getObjects() {
		return objects.toArray();
	}
	public void setObjects(Object...objs) {
		this.objects.addAll(Arrays.asList(objs));
	}
	
	

}
