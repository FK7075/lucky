package com.lucky.jacklamb.createtable;

import java.sql.Connection;
import java.util.List;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/10/23 17:27
 */
public class OracleCreateTableSqlGenerate implements CreateTableSqlGenerate {

    @Override
    public String createTableSql(String dbname, Class<?> pojoClass) {
        return null;
    }

    @Override
    public List<String> deleteKeyAndIndexSQL(String queryCreateSQL, String tableName) {
        return null;
    }

    @Override
    public List<String> addKeyAndIndexSQL(String dbname, Class<?> pojoClass) {
        return null;
    }

    @Override
    public String getDDL(Connection conn, String tableName) {
        return null;
    }
}
