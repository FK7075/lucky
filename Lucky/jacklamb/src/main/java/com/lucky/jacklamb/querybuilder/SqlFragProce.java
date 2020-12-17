package com.lucky.jacklamb.querybuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于处理Sql片段化操作的类(单例模式)
 * 
 * @author fk7075
 *
 */
public class SqlFragProce {
	
	private static SqlFragProce sqlfp;

	private SqlFragProce() {}
	
	public static SqlFragProce getSqlFP() {
		if(sqlfp==null)
			return new SqlFragProce();
		else
			return sqlfp;
	}

	// 截取个个属性对应的Sql片段
	private List<String> sqlTruncation(String sqlStr) {
		List<String> sql = new ArrayList<>();
		String dSqlStr = sqlStr.toUpperCase();
		boolean isSet = dSqlStr.contains("SET");
		boolean isWhere = dSqlStr.contains("WHERE");
		// UPDATE table SET f1=?,f2=? WHERE f3=? AND f4>?
		if (isSet && isWhere) {
			int where_start = dSqlStr.indexOf("WHERE");
			String sqlStart = sqlStr.substring(0, where_start);
			String sqlEnd = sqlStr.substring(where_start, sqlStr.length());
			sql.addAll(sqlTruncation(sqlStart));
			sql.addAll(sqlTruncation(sqlEnd));

		}
		// UPDATE table SET f1=?,f2=?
		if (isSet && !isWhere) {
			int set_start = dSqlStr.indexOf("SET");
			int set_end = set_start + 3;
			sql = charIndex(sqlStr, set_end);
			if (!sql.isEmpty()) {

			}
		}
		// SELECT * FROM table WHERE f1=? AND f2=? OR f3<?
		if (!isSet && isWhere) {
			int where_start = dSqlStr.indexOf("WHERE");
			int where_end = where_start + 5;
			sql = charIndex(sqlStr, where_end);

		}
		return sql;
	}

	private List<String> charIndex(String str, int start) {
		String strCopy = str;
		List<Integer> indexlist = new ArrayList<>();
		List<String> strlist = new ArrayList<>();
		indexlist.add(start);
		while (str.contains("?")) {
			int i = str.indexOf("?");
			str = str.replaceFirst("\\?", "#");
			indexlist.add(i);
		}
		for (int i = 0; i < indexlist.size() - 1; i++) {
			if (i == 0) {
				strlist.add(strCopy.substring(indexlist.get(i), indexlist.get(i + 1) + 1));
			} else {
				strlist.add(strCopy.substring(indexlist.get(i) + 1, indexlist.get(i + 1) + 1));
			}
		}
		return strlist;
	}

	private String exceptionSqlToNormal(String sqlStr) {
		String sql_exc = sqlStr.toUpperCase().replaceAll(" ", "");
		if (sql_exc.contains("SET")&&(!sql_exc.contains("LIMIT"))) {
			int set_end = sql_exc.indexOf("SET") + 3;
			String dou=sql_exc.substring(set_end, set_end+1);
			if (",".equals(dou)) {
				sqlStr = sqlStr.replaceFirst(",", " ");
			}
		}
		if (sql_exc.contains("WHERE")&&(!sql_exc.contains("LIMIT"))) {
			int where_end = sql_exc.indexOf("WHERE") + 5;
			if (where_end == sql_exc.length()) {
				sqlStr = sqlStr.replaceFirst("(?i)WHERE", "");
			} else {
				String info_or = sql_exc.substring(where_end, where_end + 2);
				String info_and = sql_exc.substring(where_end, where_end + 3);
				if ("AND".equals(info_and)) {
					sqlStr = sqlStr.replaceFirst("(?i)AND", "");
				} else if ("OR".equals(info_or)) {
					sqlStr = sqlStr.replaceFirst("(?i)OR", "");
				}
			}

		}
		return sqlStr;
	}

	// 返回处理后的SQL和参数数组Map<String,Oject[]>
	public  SqlAndObject filterSql(String sqlStr, Object... objs) {
		List<Object> objlist = new ArrayList<>();
		SqlAndObject sqlandobj = new SqlAndObject();
		List<String> sqlFragment = sqlTruncation(sqlStr);
		if(objs!=null)
			for (int i = 0; i < objs.length; i++) {
				if (objs[i] == null) {
					sqlStr = sqlStr.replaceFirst(sqlFragment.get(i) + "\\?", "");
				} else {
					objlist.add(objs[i]);
				}
			}
			sqlandobj.setSqlStr(exceptionSqlToNormal(sqlStr));
			sqlandobj.setObjects( objlist.toArray());
		return sqlandobj;

	}

}
