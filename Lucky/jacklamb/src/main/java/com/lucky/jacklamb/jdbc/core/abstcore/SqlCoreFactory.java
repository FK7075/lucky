package com.lucky.jacklamb.jdbc.core.abstcore;


import com.lucky.jacklamb.datasource.LuckyDataSource;
import com.lucky.jacklamb.datasource.LuckyDataSourceManage;
import com.lucky.jacklamb.exception.DatabaseTypeUnableIdentifyException;
import com.lucky.jacklamb.jdbc.core.dynamiccoreImpl.*;
import com.lucky.jacklamb.jdbc.core.fixedcoreImpl.StatementCoreImpl;
import com.lucky.jacklamb.jdbc.potable.PojoManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlCoreFactory {

	private static final Logger log= LoggerFactory.getLogger(SqlCoreFactory.class);

	private static Map<String,SqlCore> sqlCoreMap;

	private static List<LuckyDataSource> dataSources;

	static {
		if(sqlCoreMap==null)
			sqlCoreMap=new HashMap<>();
		if(dataSources==null){
			dataSources= LuckyDataSourceManage.getAllDataSource();
		}
	}
	
	public static SqlCore createSqlCore() {
		return createSqlCore("defaultDB");
	}
	
	public static SqlCore createSqlCore(String dbname) {
		if(sqlCoreMap.containsKey(dbname))
			return sqlCoreMap.get(dbname);
		SqlCore sqlCore=createBaseSqlCore(dbname);
		sqlCore.setStatementCore(StatementCoreImpl.getDefaultStatementCoreImpl(sqlCore.getDataSource()));
		sqlCoreMap.put(dbname,sqlCore);
		return sqlCore;
	}

	public static SqlCore createTransactionSqlCore() {
		return createTransactionSqlCore("defaultDB");
	}

	public static SqlCore createTransactionSqlCore(String dbname) {
		SqlCore sqlCore=createBaseSqlCore(dbname);
		StatementCoreImpl transactionStatementCoreImpl = StatementCoreImpl.getTransactionStatementCoreImpl(sqlCore.getDataSource());
		sqlCore.setStatementCore(transactionStatementCoreImpl);
		return sqlCore;
	}

	private static SqlCore createBaseSqlCore(String dbname){
		String dbType= PojoManage.getDatabaseType(dbname);
		StringBuilder sb=new StringBuilder();
		SqlCore sqlCore;
		switch (dbType) {
			case "MYSQL":
				log.debug(sb.append("Create SqlCore ==> dbType=MySql,dbname=").append(dbname).append(",class=").append(MySqlCore.class).toString());
				sqlCore= new MySqlCore(dbname);
				break;
			case "DB2":
				log.debug(sb.append("Create SqlCore ==> dbType=DB2,dbname=").append(dbname).append(",class=").append(DB2Core.class).toString());
				sqlCore= new DB2Core(dbname);
				break;
			case "ORACLE":
				log.debug(sb.append("Create SqlCore ==> dbType=Oracle,dbname=").append(dbname).append(",class=").append(OracleCore.class).toString());
				sqlCore= new OracleCore(dbname);
				break;
			case "POSTGRESQL":
				log.debug(sb.append("Create SqlCore ==> dbType=PostgreSql,dbname=").append(dbname).append(",class=").append(PostgreSqlCore.class).toString());
				sqlCore= new PostgreSqlCore(dbname);
				break;
			case "SQL SERVER":
				log.debug(sb.append("Create SqlCore ==> dbType=Sql Server,dbname=").append(dbname).append(",class=").append(SqlServerCore.class).toString());
				sqlCore= new SqlServerCore(dbname);
				break;
			case "SYBASE":
				log.debug(sb.append("Create SqlCore ==> dbType=Sybase,dbname=").append(dbname).append(",class=").append(SybaseCore.class).toString());
				sqlCore= new SybaseCore(dbname);
				break;
			case "ACCESS":
				log.debug(sb.append("Create SqlCore ==> dbType=Access,dbname=").append(dbname).append(",class=").append(AccessSqlCore.class).toString());
				sqlCore= new AccessSqlCore(dbname);
				break;
			default:
				log.error("无法识别的数据库类型，Lucky目前还不支持该类型的数据库驱动 : "+ LuckyDataSourceManage.getDataSource(dbname).getDriverClass());
				throw new DatabaseTypeUnableIdentifyException("Lucky目前还不支持该类型的数据库，我们正在拼命更新中！DatabaseType:"+ LuckyDataSourceManage.getDataSource(dbname).getDriverClass());
		}
		return sqlCore;
	}
}
