package com.lucky.jacklamb.jdbc.core;

import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.jacklamb.cache.Cache;
import com.lucky.jacklamb.exception.LuckySqlOperationException;
import com.lucky.jacklamb.jdbc.conversion.JDBCConversion;
import com.lucky.jacklamb.log.SqlLog;
import com.lucky.jacklamb.jdbc.core.sql.CreateSql;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC相关操作类
 * @author fk-7075
 */
@SuppressWarnings("unchecked")
public class SqlOperation {

	private Connection conn;
	private String dbname;
	public static Map<String, Cache<String,List<Map<String,Object>>>> resultCache =new HashMap<>();
	private boolean isCache;
	private boolean isFullMap=false;

	public void setFullMap(boolean fullMap) {
		isFullMap = fullMap;
	}

	public SqlOperation(Connection conn, String dbname, boolean isFullMap) {
		this.conn = conn;
		this.dbname = dbname;
		this.isFullMap=isFullMap;
//		isCache= ReaderInI.getDataSource(dbname).getCache();
		//如果用户开启了缓存配置，则初始化一个LRU缓存
//		if(isCache&&!resultCache.containsKey(dbname)){
//			Cache<String,List<Map<String,Object>>> dbCache= CacheFactory.getCache(dbname);
//			resultCache.put(dbname,dbCache);
//		}
	}

	/**
	 * 实现对表的曾刪改操作
	 * @param sql（预编译的sql语句）
	 * @param obj（替换占位符的数组）
	 * @return boolean
	 */
	public int setSql(String sql, Object...obj) {
		PreparedStatement ps=null;
		try {
			ps = conn.prepareStatement(sql);
			for (int i = 0,count=obj.length; i <count; i++) {
				ps.setObject(i + 1, obj[i]);
			}
			int result = ps.executeUpdate();
			new SqlLog(dbname).isShowLog(sql, obj);
			clearCache();
			return result;
		} catch (SQLException e) {
			throw new LuckySqlOperationException(dbname,sql,obj,e);
		} finally {
			LuckyDataSource.close(null,ps,null);
		}
	}

	/**
	 * 增删改操作批处理
	 * @param sql 预编译的SQL语句
	 * @param obj 预编译的SQL语句
	 * @return
	 */
	public int[] setSqlBatch(String sql,Object[]... obj) {
		PreparedStatement ps=null;
		try {
			ps = conn.prepareStatement(sql);
			if(obj==null||obj.length==0) {
				int[] result={ps.executeUpdate()};
				return result;
			}else {
				for(int i=0;i<obj.length;i++) {
					for(int j=0,count=obj[i].length;j<count;j++) {
						ps.setObject(j+1, obj[i][j]);
					}
					ps.addBatch();
				}
				int[] result = ps.executeBatch();
				new SqlLog(dbname).isShowLog(sql, obj);
				clearCache();
				return  result;
			}
		} catch (SQLException e) {
			throw new LuckySqlOperationException(dbname,sql,obj,e);
		}finally {
			LuckyDataSource.close(null,ps,null);
		}
	}

	/**
	 * SQL批量执行
	 * @param sqls 一系列完整SQL组成的数组
	 * @return
	 */
	public int[] setSqlBatch(String...sqls){
		Statement ps=null;
		try {
			ps = conn.createStatement();
			for (String sql : sqls) {
				ps.addBatch(sql);
			}
			int[] result = ps.executeBatch();
			new SqlLog(dbname).isShowLog(sqls);
			clearCache();
			return result;
		} catch (SQLException e) {
			throw new LuckySqlOperationException(sqls,e);
		}finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行SQL返回结果集ResultSet
	 * @param sql 预编译SQL
	 * @param obj 预编译SQl执行参数
	 * @return
	 */
	public ResultSet getResultSet(String sql, Object...obj) {
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			for (int i = 0,count=obj.length; i <count; i++) {
				ps.setObject(i + 1, obj[i]);
			}
			ResultSet resultSet = ps.executeQuery();
			new SqlLog(dbname).isShowLog(sql, obj);
			return resultSet;
		} catch (SQLException e) {
			throw new LuckySqlOperationException(dbname,sql,obj,e);
		}
	}



	/**
	 *
	 * @param c 包装类的Class对象
	 * @param sql 预编译的sql语句
	 * @param obj 替换占位符的数组
	 * @param <T>
	 * @return
	 */
	public <T> List<T> autoPackageToList(Class<T> c, String sql, Object... obj) {
		if(isCache){
			return JDBCConversion.conversion(dbname,getCacheQueryResult(sql,obj),c,isFullMap,conn);
		}
		return JDBCConversion.conversion(dbname,getQueryResult(sql,obj),c,isFullMap,conn);
	}

	public void clearCache(){
		if(isCache) {
			resultCache.get(dbname).clear();
		}
	}

	public List<Map<String,Object>> getCacheQueryResult(String sql, Object...obj){
		String completeSql= CreateSql.getCompleteSql(sql,obj);
		if(resultCache.get(dbname).containsKey(completeSql)){
			return resultCache.get(dbname).get(completeSql);
		}else{
			List<Map<String, Object>> queryResult = getQueryResult(sql, obj);
			if(!queryResult.isEmpty()){
				resultCache.get(dbname).put(completeSql,queryResult);
			}
			return queryResult;
		}
	}

	public List<Map<String,Object>> getQueryResult(String sql, Object...obj){
		List<Map<String,Object>> queryResult = new ArrayList<>();
		PreparedStatement ps=null;
		SqlLog log=new SqlLog(dbname);
		ResultSet rs=null;
		try{
			ps = conn.prepareStatement(sql);
			for (int i = 0,count=obj.length; i <count; i++) {
				ps.setObject(i + 1, obj[i]);
			}
			rs = ps.executeQuery();
			log.isShowLog(sql, obj);
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while (rs.next()){
				Map<String,Object> rowData = new HashMap<>();
				for (int i = 1; i <= columnCount; i++) {
					rowData.put(md.getColumnLabel(i).toUpperCase(), rs.getObject(i));
				}
				queryResult.add(rowData);
			}
			return queryResult;
		}catch (SQLException e){
			throw new LuckySqlOperationException(dbname,sql,obj,e);
		}finally {
			LuckyDataSource.close(rs,ps,null);
		}
	}
}
