package com.lucky.jacklamb.jdbc.core.sql;

import java.util.Arrays;
import java.util.List;

public class CreateSql {
	
	private String sql=null;

	private static List<String> typeList;

	static{
		String[] types={"int","Integer","double","Double","long","Long","float","Float","short","Short"};
		typeList= Arrays.asList(types);
	}
	
	
	/**
	 * 获得缓存的key值(sql语句)
	 * @param sql1
	 * @param obj
	 * @return
	 */
	public String getSqlString(String sql1,Object... obj) {
		if(obj==null||obj.length==0) {
			sql=sql1;
		}else {
			String incompleteSql=sql1;
			for (Object value :obj) {
				int i =incompleteSql.indexOf("?");
				StringBuffer buf=new StringBuffer(incompleteSql);
				buf.replace(i, i+1, value+"");
				incompleteSql=buf.toString();
			}
			sql=incompleteSql;
		}
		return sql;
	}

	public static String getCompleteSql(String precompileSql,Object...params){
		if(params.length==0)
			return precompileSql;
		String type,sqlParam;
		for (Object param : params) {
			if(param==null){
				sqlParam="NULL";
			}else{
				type=param.getClass().getSimpleName();
				if(typeList.contains(type)){
					sqlParam=param.toString();
				}else{
					sqlParam="'"+param.toString()+"'";
				}
			}
			precompileSql=precompileSql.replaceFirst("\\?",sqlParam.replaceAll("\\$","LUCKY_JACK_LUCY_LUCKY_OK"));
		}
		return precompileSql.replace("LUCKY_JACK_LUCY_LUCKY_OK","$");
	}



	public static void main(String[] args) {
		String sql="INSERT INTO user(id,name,password,age,math) VALUES （?,?,?,?,?）";
		Object[] obj={12,"JACK","PA$$W0RD",23,99.9};
		System.out.println(getCompleteSql(sql, obj));
	}

}
