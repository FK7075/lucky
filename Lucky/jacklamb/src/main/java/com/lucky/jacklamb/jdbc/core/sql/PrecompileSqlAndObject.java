package com.lucky.jacklamb.jdbc.core.sql;

import java.util.ArrayList;
import java.util.List;

public class PrecompileSqlAndObject {
	
	private String precompileSql;
	
	private List<Object> objects;
	
	public PrecompileSqlAndObject() {
		this.objects = new ArrayList<>();
	}

	public String getPrecompileSql() {
		return precompileSql;
	}

	public void setPrecompileSql(String precompileSql) {
		this.precompileSql = precompileSql;
	}

	public List<Object> getObjects() {
		return objects;
	}

	public void setObjects(List<Object> objects) {
		this.objects = objects;
	}
	
	public void addObjects(Object object) {
		objects.add(object);
	}
	
	public void addAllObjects(List<Object> objects) {
		this.objects.addAll(objects);
	}

}
