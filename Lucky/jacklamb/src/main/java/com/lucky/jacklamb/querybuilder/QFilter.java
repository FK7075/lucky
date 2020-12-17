package com.lucky.jacklamb.querybuilder;

import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.utils.reflect.ClassUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设置查询返回列
 * @author DELL
 *
 */
public class QFilter {
	

	private List<String> allFields;

	private List<String> addFields;

	private Map<String,PojoAndField> pojoAndFields;

	private String dbname;



	/**
	 * 设置查询返回列
	 * @param pojoClass
	 */
	public QFilter(Class<?> pojoClass,String dbname) {
		this.dbname=dbname;
		allFields=new ArrayList<>();
		addFields =new ArrayList<>();
		Field[] fields= ClassUtils.getAllFields(pojoClass);
		for(Field field:fields) {
			if(!PojoManage.isNoColumn(field,dbname)) {
				this.allFields.add("`"+PojoManage.getTableField(dbname,field)+"`");
			}
		}
	}

	/**
	 * query操作时使用
	 * @param pojos
	 */
	public QFilter(String dbname,Object... pojos) {
		this.dbname=dbname;
        allFields=new ArrayList<>();
        addFields =new ArrayList<>();
        setFieldAndObject(pojos);
		for(Object pojo:pojos){
			Class<?> pojoClass=pojo.getClass();
			Field[] pojoFields=ClassUtils.getAllFields(pojoClass);
			for(Field field:pojoFields){
				if(!PojoManage.isNoColumn(field,dbname)) {
					allFields.add("`"+PojoManage.tableAlias(pojoClass,dbname)+"`.`"+PojoManage.getTableField(dbname,field)+"`");
				}
			}
		}
	}
	
	/**
	 * 隐藏返回列时使用
	 * @param column
	 * @return
	 */
	public QFilter hidden(String column) {
		allFields.remove(qfieldName(column));
		return this;
	}
	
	/**
	 * 设置返回列时使用
	 * @param column
	 * @return
	 */
	public QFilter show(String column) {
		addFields.add(qfieldName(column));
		return this;
	}
	
	public String lines() {
		StringBuilder sb=new StringBuilder();
		if(addFields.isEmpty()) {
			for(String col:allFields) {
				sb.append(col).append(",");
			}
			return sb.substring(0, sb.length()-1);
		}else {
			for(String col: addFields) {
				sb.append(col).append(",");
			}
			return sb.substring(0, sb.length()-1);
		}
	}

	public String qfieldName(String input){
        int i = afterMatching(input);
        if(i==0) {
			throw new RuntimeException("没有找到与\""+input+"\"字段所匹配的属性！");
		}
        if(i>1) {
			throw new RuntimeException("存在多个与\""+input+"\"字段所匹配的实体！请您标明该字段所属的实体。（标明方法：实体.属性）");
		}
        if(allFields.contains(input)) {
			return input;
		}
        for(String fd:allFields){
            int start=fd.indexOf(".");
            String mfd=fd.substring(start+1);
            if(mfd.equals(input)) {
				return fd;
			}
        }
        return null;
    }

	public int afterMatching(String field){
	    int s=0;
	    if(field.contains(".")){
	        if(allFields.contains(field)) {
				return 1;
			}
	        return 0;
        }
	    for(String fd:allFields){
	        int start=fd.indexOf(".");
	        String mfd=fd.substring(start+1);
	        if(mfd.equals(field)){
				s++;
				if(s==2) {
					return s;
				}
			}
        }
	    return s;
    }

	public String like(String...likeField){
	    String resut="";
	    boolean first=true;
	    for(String like:likeField){
            String key=qfieldName(like);
            if(first){
                first=false;
                resut+=" "+key+" LIKE "+pojoAndFields.get(key).getValue();
                pojoAndFields.get(key).setValue();
            }else{
                resut+=" AND "+key+" LIKE "+pojoAndFields.get(key).getValue();
                pojoAndFields.get(key).setValue();
            }
        }
        return resut;
    }

    public void setFieldAndObject(Object...pojos){
	    pojoAndFields=new HashMap<>();
	    for(Object pojo:pojos){
	        Class<?> pojoClass=pojo.getClass();
	        Field[] fields=ClassUtils.getAllFields(pojoClass);
	        for(Field f:fields){
	            String key=PojoManage.tableAlias(pojoClass,dbname)+"."+PojoManage.getTableField(dbname,f);
                pojoAndFields.put(key,new PojoAndField(pojo,f));
            }
        }
    }

    public String sort(List<SortSet> sortSets){
		String resut="";
		boolean first=true;
		for(SortSet sst:sortSets){
			if(first){
				first=false;
				resut+=" ORDER BY "+qfieldName(sst.getField())+" "+sst.getSort().getSort();
			}else{
				resut+=",ORDER BY "+qfieldName(sst.getField())+" "+sst.getSort().getSort();
			}
		}
		return resut;
	}

}

class PojoAndField{
    private Object pojo;
    private Field field;

    public PojoAndField(Object pojo, Field field) {
        this.pojo = pojo;
        this.field = field;
    }

    public Object getPojo() {
        return pojo;
    }

    public void setPojo(Object pojo) {
        this.pojo = pojo;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getValue(){
        field.setAccessible(true);
        Object o = null;
        try {
            o = field.get(pojo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o.toString();
    }

    public void setValue(){
        field.setAccessible(true);
        try {
            field.set(pojo,null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}