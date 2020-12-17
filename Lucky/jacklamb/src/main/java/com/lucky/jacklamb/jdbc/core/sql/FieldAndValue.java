package com.lucky.jacklamb.jdbc.core.sql;

import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class FieldAndValue {

	private String idField;

	private Object idValue;

	private Map<String, Object> fieldNameAndValue;
	
	private Object pojo;

	private String dbname;

	public String getIdField() {
		return idField;
	}

	public void setIdField(String idField) {
		this.idField = idField;
	}

	public Object getIdValue() {
		return idValue;
	}

	public void setIdValue(Object idValue) {
		this.idValue = idValue;
	}

	public Map<String, Object> getFieldNameAndValue() {
		return fieldNameAndValue;
	}

	public void setFieldNameAndValue(Map<String, Object> fieldNameAndValue) {
		this.fieldNameAndValue = fieldNameAndValue;
	}

	public FieldAndValue(Object pojo, String dbname) {
		this.dbname=dbname;
		this.pojo=pojo;
		setIDField(pojo);
		setNotNullFields(pojo);
	}
	
	public boolean containsField(String field) {
		return fieldNameAndValue.containsKey(field);
	}
	
	public boolean containsFields(String...fields) {
		for(String str:fields) {
			if(!containsField(str))
				throw new RuntimeException("传入的"+pojo.getClass().getName()+"对象"+pojo.toString()+"的非空属性映射中不包含\""+str+"\",无法完成更新操作");
		}
		return true;
	}

	public void setIDField(Object pojo){
		Class<?> pojoClass = pojo.getClass();
		Field id = PojoManage.getIdField(pojoClass);
		this.idField = PojoManage.getTableField(dbname,id);
		this.idValue= FieldUtils.getValue(pojo,id);
	}

	public void setNotNullFields(Object pojo){
		fieldNameAndValue = new HashMap<>();
		Class<?> pojoClass = pojo.getClass();
		Field[] fields = ClassUtils.getAllFields(pojoClass);
		Object fieldValue;
		for (Field field : fields) {
			if(PojoManage.isNoColumn(field,dbname))
				continue;
			fieldValue=FieldUtils.getValue(pojo,field);
			if (fieldValue != null)
				fieldNameAndValue.put(PojoManage.getTableField(dbname,field), fieldValue);
		}
	}
}
