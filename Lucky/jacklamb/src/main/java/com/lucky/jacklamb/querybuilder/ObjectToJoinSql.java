package com.lucky.jacklamb.querybuilder;

import com.lucky.jacklamb.jdbc.core.abstcore.SqlGroup;
import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;
import com.lucky.utils.regula.Regular;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ObjectToJoinSql{

	private static final String ANGLE="<([\\d]*?)(L|R|)?>";

	private static final String LINE="--(R|L)?";

	private static final String ARROW="-->(R|L)?";

	/**
	 * 需要操作的对象
	 */
	private Object[] obj;

	/**
	 * 返回列
	 */
	private String result;

	/**
	 * 排序条件
	 */
	private String sort;

	/**
	 * 模糊条件
	 */
	private String like;

	private SqlGroup sqlGroup;

	private String dbname;

	public ObjectToJoinSql(QueryBuilder query) {
		this.obj = query.getObjectArray();
		this.sort=query.getSort();
        this.like=query.getLike();
        this.sqlGroup=query.getWheresql();
        this.result=query.getResult();
        this.dbname=query.getDbname();
	}

	/**
	 * 得到AND 部分的SQL
	 * @return
	 */
	private String andFragment() {
		StringBuilder sql=new StringBuilder();
		int p = 0;
		for (int i = 0; i < obj.length; i++) {
			Class<?> clzz = obj[i].getClass();
			Field[] fields = ClassUtils.getAllFields(clzz);
			for (int j = 0; j < fields.length; j++) {
				if(PojoManage.isNoColumn(fields[j],dbname))
					continue;
				Object fk= FieldUtils.getValue(obj[i],fields[j]);
				if (fk != null) {
					if (p == 0) {
						sql.append(" WHERE ").append(PojoManage.tableAlias(clzz,dbname))
								.append(".").append(PojoManage.getTableField(dbname,fields[j]))
								.append("=?");
						p++;
					} else {
						sql.append(" AND ").append(PojoManage.tableAlias(clzz,dbname))
								.append(".").append(PojoManage.getTableField(dbname,fields[j]))
								.append("=?");

					}
				}
			}
		}
		return sql.toString();
	}

	/**
	 * 得到连接操作的查询条件
	 * @return
	 */
	public Object[] getJoinObject() {
		List<Object> list = new ArrayList<>();
		for (int i = 0; i < obj.length; i++) {
			Field[] fields = obj[i].getClass().getDeclaredFields();
			try {
				for (int j = 0; j < fields.length; j++) {
					fields[j].setAccessible(true);
					Object object = fields[j].get(obj[i]);
					if (object != null) {
						list.add(object);
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return list.toArray();
	}

	/**
	 * 得到连接操作的SQL
	 * 
	 * @return
	 */
	public String getJoinSql(String...expression) {
	    String where=andFragment();
	    if(!"".equals(where)&&!"".equals(like))
	        like=" AND "+like;
	    return sqlGroup.sqlGroup(result, getOnSql(expression), andFragment(), like, sort);
	}




	
	/**
	 * 根据连接表达式确定连接方式
	 * @param expression
	 * @return
	 */
	public String getOnSql(String...expression) {
		String expre="";
		if(expression.length==0||"".equals(expression[0])) {
			for(Object object:obj) {
				expre+=PojoManage.getTable(object.getClass(),dbname)+"-->";
			}
			expre=expre.substring(0,expre.length()-3);
		}else {
			expre=expression[0];
		}
		String onsql="";
		List<ClassControl> parsExpression = parsExpression(expre);
		for(int i=0;i<parsExpression.size();i++) {
			if(i==0) {
				onsql+=PojoManage.selectFromTableAlias(parsExpression.get(0).getClzz(),dbname);
			}else {
				onsql+=parsExpression.get(i).getJoin()+PojoManage.selectFromTableAlias(parsExpression.get(i).getClzz(),dbname)+" ON "+getEquation(parsExpression.get(i).getClzz(),parsExpression.get(i-1-parsExpression.get(i).getSite()).getClzz());
			}
		}
		return onsql;
	}

	/**
	 * 两个Class确定连接等式
	 * @param clax
	 * @param clay
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getEquation(Class<?> clax,Class<?> clay) {
		try {
			List<Class<?>> claxKeyClasss = (List<Class<?>>) PojoManage.getKeyFields(clax, dbname,false);
			if(claxKeyClasss.contains(clay))
				return PojoManage.tableAlias(clax,dbname)+"."+PojoManage.getTableField(dbname,PojoManage.classToField(clax, clay, dbname))+"="+PojoManage.tableAlias(clay,dbname)+"."+PojoManage.getIdString(clay, dbname);
			return PojoManage.tableAlias(clay,dbname)+"."+PojoManage.getTableField(dbname,PojoManage.classToField(clay, clax, dbname))+"="+PojoManage.tableAlias(clax,dbname)+"."+PojoManage.getIdString(clax, dbname);
		}catch(Exception e) {
			throw new RuntimeException(clax.getName()+" 与  "+clay.getName()+"不存在'主外键关系',请检查您的相关配置(@Key,@Id，连接查询表达式['->' '--' '<?>'] )....",e);
		}
	}
	
	/**
	 * 解析表达式
	 * @param expression
	 * @return
	 */
	public List<ClassControl> parsExpression(String expression){
		 List<ClassControl> cctlist=new ArrayList<>();
		String order=expression.replaceAll(ANGLE, ",").replaceAll(ARROW, ",").replaceAll(LINE, ",").toLowerCase();
		String symbol=expression.toLowerCase();
		String[] splitName = order.split(",");
		for(String name:splitName) {
			symbol=symbol.replaceAll(name, ",");
			ClassControl cctl=new ClassControl();
			Stream.of(obj).map(obj->obj.getClass()).filter(c->name.equals(PojoManage.getTable(c,dbname))).forEach(cctl::setClzz);
			cctlist.add(cctl);
		}
		String[] symArr=symbol.split(",");
		for(int i=0;i<symArr.length;i++) {
			symbolToInt(cctlist.get(i),symArr[i]);
		}
		return cctlist;
	}
	
	public void symbolToInt(ClassControl cct,String symbol) {
		if("".equals(symbol)) {
			cct.setSite(-1);
		}else if("--".equals(symbol)) {
			cct.setSite(1);
		}else if("--R".equals(symbol)) {
			cct.setSite(1);
			cct.setJoin(" RIGHT JOIN ");
		}else if("--L".equals(symbol)) {
			cct.setSite(1);
			cct.setJoin(" LEFT JOIN ");
		}else if("-->".equals(symbol)) {
			cct.setSite(0);
		}else if("-->R".equals(symbol)) {
			cct.setSite(0);
			cct.setJoin(" RIGHT JOIN ");
		}else if("-->L".equals(symbol)) {
			cct.setSite(0);
			cct.setJoin(" LEFT JOIN ");
		}else if(Regular.check(symbol,ANGLE)) {
			symbol=symbol.substring(1,symbol.length()-1);
			if(symbol.endsWith("R")){
				cct.setSite(Integer.parseInt(symbol.substring(0,symbol.length()-1)));
				cct.setJoin(" RIGHT JOIN ");
			}else if(symbol.endsWith("L")){
				cct.setSite(Integer.parseInt(symbol.substring(0,symbol.length()-1)));
				cct.setJoin(" LEFT JOIN ");
			}
		}else {
			cct.setSite(-2);
		}
		
	}
}

class ClassControl{
	
	private Class<?> clzz;
	
	private int site=-1;

	private String join=" JOIN ";

	public String getJoin() {
		return join;
	}

	public void setJoin(String join) {
		this.join = join;
	}

	public Class<?> getClzz() {
		return clzz;
	}

	public void setClzz(Class<?> clzz) {
		this.clzz = clzz;
	}

	public int getSite() {
		return site;
	}

	public void setSite(int site) {
		this.site = site;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ClassControl{");
		sb.append("clzz=").append(clzz);
		sb.append(", site=").append(site);
		sb.append(", join='").append(join).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
