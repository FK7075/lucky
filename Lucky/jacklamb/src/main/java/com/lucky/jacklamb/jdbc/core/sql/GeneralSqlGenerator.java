package com.lucky.jacklamb.jdbc.core.sql;

import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.jacklamb.querybuilder.QFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 通用SQL生成器
 * @author DELL
 *
 */
public class GeneralSqlGenerator {

	private String dbname;

	public GeneralSqlGenerator(String dbname){
		this.dbname=dbname;
	}
	
	/**
	 * id查询的SQL
	 * @param c
	 * @return
	 */
	public String getOneSql(Class<?> c) {
		StringBuilder sql=new StringBuilder("SELECT ");
		sql.append(new QFilter(c,dbname).lines()).append(" FROM ").append("`")
		.append(PojoManage.getTable(c,dbname)).append("`").append(" WHERE ").append("`")
		.append(PojoManage.getIdString(c,dbname)).append("`").append(" =?");
		return sql.toString();
	}
	
	/**
	 * id删除的SQL
	 * @param c
	 * @return
	 */
	public String deleteOneSql(Class<?> c) {
		StringBuilder sql=new StringBuilder("DELETE FROM ");
		sql.append("`").append(PojoManage.getTable(c,dbname)).append("`").append(" WHERE ").append("`")
		.append(PojoManage.getIdString(c,dbname)).append("`").append(" =?");
		return sql.toString();
	}
	
	/**
	 * 批量id删除的SQL
	 * @param c
	 * @param ids
	 * @return
	 */
	public String deleteIn(Class<?> c,Object[] ids) {
		return inSql(c,ids,true);
	}

	/**
	 * 批量id查询的SQL
	 * @param c
	 * @param ids
	 * @return
	 */
	public String selectIn(Class<?> c,Object[] ids){
		return inSql(c,ids,false);
	}

	private String inSql(Class<?> c,Object[]ids,boolean isDel){
		boolean first=true;
		StringBuilder sql;
		if(isDel){
			sql=new StringBuilder("DELETE FROM ");
		}else{
			sql=new StringBuilder("SELECT * FROM ");
		}
		sql.append("`").append(PojoManage.getTable(c,dbname)).append("`").append(" WHERE ")
				.append("`").append(PojoManage.getIdString(c,dbname)).append("`").append(" IN ");
		for(int i=0;i<ids.length;i++) {
			if(first) {
				sql.append("(?");
				first=false;
			}else {
				sql.append(",?");
			}
		}
		sql.append(")");
		return sql.toString();
	}
	
	/**
	 * 简单对象COUNT的SQL
	 * @param pojo
	 * @return
	 */
	public PrecompileSqlAndObject singleCount(Object pojo) {
		StringBuilder sql=new StringBuilder("SELECT COUNT(");
		sql.append("`").append(PojoManage.getIdString(pojo.getClass(),dbname)).append("`").append(")").append(" FROM ").append("`").append(PojoManage.getTable(pojo.getClass(),dbname)).append("`");
		PrecompileSqlAndObject psaq=singleWhere(pojo);
		psaq.setPrecompileSql(sql.append(psaq.getPrecompileSql()).toString());
		return psaq;
	}
	
	/**
	 * 简单对象查询的SQL
	 * @param pojo
	 * @return
	 */
	public PrecompileSqlAndObject singleSelect(Object pojo) {
		Class<?> objClass=pojo.getClass();
		StringBuilder sql=new StringBuilder("SELECT ");
		sql.append(new QFilter(objClass,dbname).lines()).append(" FROM ").append("`").append(PojoManage.getTable(objClass,dbname)).append("`");
		PrecompileSqlAndObject psaq=singleWhere(pojo);
		psaq.setPrecompileSql(sql.append(psaq.getPrecompileSql()).toString());
		return psaq;
	}

	/**
	 * 简单对象删除的SQL
	 * @param pojo
	 * @return
	 */
	public PrecompileSqlAndObject singleDelete(Object pojo) {
		StringBuilder sql=new StringBuilder("DELETE FROM ");
		sql.append("`").append(PojoManage.getTable(pojo.getClass(),dbname)).append("`");
		PrecompileSqlAndObject psaq=singleWhere(pojo);
		psaq.setPrecompileSql(sql.append(psaq.getPrecompileSql()).toString());
		return psaq;
	}
	
	/**
	 * 简单对象添加的SQL
	 * @param pojo
	 * @return
	 */
	public PrecompileSqlAndObject singleInsert(Object pojo){
		PrecompileSqlAndObject psaq=new PrecompileSqlAndObject();
		FieldAndValue fv=new FieldAndValue(pojo,dbname);
		boolean first=true;
		StringBuilder insertSql=new StringBuilder("INSERT INTO ");
		insertSql.append("`").append(PojoManage.getTable(pojo.getClass(),dbname)).append("`").append("(");
		StringBuilder valuesSql=new StringBuilder(" VALUES(");
		Map<String, Object> fvMap = fv.getFieldNameAndValue();
		for(Entry<String, Object> entry:fvMap.entrySet()) {
			if(first) {
				insertSql.append("`").append(entry.getKey()).append("`");
				valuesSql.append("?");
				first=false;
			}else {
				insertSql.append(",").append("`").append(entry.getKey()).append("`");
				valuesSql.append(",?");
			}
			psaq.addObjects(entry.getValue());
		}
		psaq.setPrecompileSql(insertSql.append(")").append(valuesSql.append(")")).toString());
		return psaq;
	}
	
	/**
	 * 简单对象更新的SQL
	 * @param pojo
	 * @param conditions
	 * @return
	 */
	public PrecompileSqlAndObject singleUpdate(Object pojo,String...conditions) {
		PrecompileSqlAndObject psao=new PrecompileSqlAndObject();
		FieldAndValue fv=new FieldAndValue(pojo,dbname);
		StringBuilder updateSql=new StringBuilder("UPDATE ");
		updateSql.append("`").append(PojoManage.getTable(pojo.getClass(),dbname)).append("`").append(" SET ");
		StringBuilder whereSql=new StringBuilder();
		Map<String, Object> fvMap = fv.getFieldNameAndValue();
		if(conditions.length==0) {
			boolean first=true;
			whereSql.append(" WHERE ").append("`").append(PojoManage.getIdString(pojo.getClass(),dbname)).append("`").append("=?");
			for(Entry<String,Object> entry:fvMap.entrySet()) {
				if(!fv.getIdField().equals(entry.getKey())) {
					if(first) {
						updateSql.append("`").append(entry.getKey()).append("`").append("=?");
						first=false;
					}else {
						updateSql.append(",").append("`").append(entry.getKey()).append("`").append("=?");
					}
					psao.addObjects(entry.getValue());
				}
			}
			psao.setPrecompileSql(updateSql.append(whereSql).toString());
			psao.addObjects(fv.getIdValue());
			return psao;
		}else {
			boolean setfirst=true,wherefirst=true;
			List<Object> whereObject=new ArrayList<>();
			if(fv.containsFields(conditions)) {
				for(Entry<String,Object> entry:fvMap.entrySet()) {
					if(Arrays.asList(conditions).contains(entry.getKey())) {//WHERE
						if(wherefirst) {
							whereSql.append(" WHERE ").append("`").append(entry.getKey()).append("`").append("=?");
							wherefirst=false;
						}else {
							whereSql.append(" AND ").append("`").append(entry.getKey()).append("`").append("=?");
						}
						whereObject.add(entry.getValue());
					}else {//SET
						if(setfirst) {
							updateSql.append("`").append(entry.getKey()).append("`").append("=?");
							setfirst=false;
						}else {
							updateSql.append(",").append("`").append(entry.getKey()).append("`").append("=?");
						}
						psao.addObjects(entry.getValue());
					}
				}
				psao.addAllObjects(whereObject);
				psao.setPrecompileSql(updateSql.append(whereSql).toString());
				return psao;
			}
			return null;
		}
	}
	
	
	/**
	 * 简单对象操作的通用WHER片段SQL
	 * @param pojo
	 * @return
	 */
	public PrecompileSqlAndObject singleWhere(Object pojo) {
		FieldAndValue fv=new FieldAndValue(pojo,dbname);
		PrecompileSqlAndObject psaq=new PrecompileSqlAndObject();
		StringBuilder sql=new StringBuilder();
		boolean first=true;
		for(Entry<String, Object> entry:fv.getFieldNameAndValue().entrySet()) {
			if(first) {
				sql.append(" WHERE ").append("`").append(entry.getKey()).append("`").append("=?");
				first=false;
			}else {
				sql.append(" AND ").append("`").append(entry.getKey()).append("`").append("=?");
			}
			psaq.addObjects(entry.getValue());
		}
		psaq.setPrecompileSql(sql.toString());
		return psaq;
	}
	
}


