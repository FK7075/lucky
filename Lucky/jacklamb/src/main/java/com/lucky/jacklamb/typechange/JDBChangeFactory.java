package com.lucky.jacklamb.typechange;

import com.lucky.jacklamb.datasource.LuckyDataSourceManage;
import com.lucky.jacklamb.exception.NotSupportDataBasesException;

public class JDBChangeFactory {
	
	/**
	 * 获取数据库的类型转换工具
	 * @param dbname
	 * @return
	 */
	public static TypeConversion jDBChangeFactory(String dbname) {
		String jdbcDriver= LuckyDataSourceManage.getDataSource(dbname).getDriverClass();
		if(jdbcDriver.contains("mysql"))
			return new MySqlJavaChange();
		if(jdbcDriver.contains("db2"))
			return new DB2JavaChange();
		if(jdbcDriver.contains("oracle"))
			return new OracleJavaChange();
		if(jdbcDriver.contains("postgresql"))
			return new PostgreJavaChange();
		if(jdbcDriver.contains("sqlserver"))
			return new SqlServerJavaChange();
		if(jdbcDriver.contains("sybase"))
			return new SyBaseJavaChange();
		if(jdbcDriver.contains("access"))
			return new AccessJavaChange();
		throw new NotSupportDataBasesException("Lucky现不支持该驱动对应的数据库！ driverClass:"+jdbcDriver);
	}

}
