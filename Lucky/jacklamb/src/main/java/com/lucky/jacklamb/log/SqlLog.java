package com.lucky.jacklamb.log;

import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.datasource.sql.LuckyDataSourceManage;
import com.lucky.jacklamb.jdbc.core.sql.CreateSql;
import com.lucky.utils.base.BaseUtils;
import com.lucky.utils.base.Console;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/17 0017 10:21
 */
public class SqlLog {

    private boolean log;
    private boolean showCompleteSQL;
    private boolean isFormatSqlLog;
    private String jdbcUrl;
    private String dbname;
    private SqlFormatUtil sqlFormatUtil;

    public SqlLog(String dbname){
        sqlFormatUtil=new SqlFormatUtil();
        this.dbname=dbname;
        LuckyDataSource dataSource = LuckyDataSourceManage.getDataSource(dbname);
        jdbcUrl=dataSource.getJdbcUrl();
        jdbcUrl=jdbcUrl.contains("?")?jdbcUrl.substring(0,jdbcUrl.indexOf("?")):jdbcUrl;
        log=dataSource.getLog();
        showCompleteSQL=dataSource.getShowCompleteSQL();
        isFormatSqlLog=dataSource.getFormatSqlLog();

    }

    public void isShowLog(String sql, Object[] obj) {
        if(log) {
            log(sql,obj);
        }
    }

    public void isShowLog(String sql,Object obj[][]) {
        if(log) {
            logBatch(sql,obj);
        }
    }

    public void isShowLog(String[] sqls){
        if(log) {
            logBatch(sqls);
        }
    }

    //打印SQL日志
    private void log(String sql, Object[] obj) {
        StringBuilder sb=new StringBuilder("\nTime        : ").append(BaseUtils.time()).append("\nDatabase    : ").append("<"+dbname+"> ");
        sb.append(jdbcUrl).append("\n").append("SQL         : ").append(formatSql(sql));
        if (obj == null||obj.length==0) {
            sb.append("\nParameters  : { }");
        } else {
            sb.append("\nParameters  : { ");
            for (Object o : obj) {
                sb.append("(").append((o!=null?o.getClass().getSimpleName():"NULL")).append(")").append(o).append("   ");
            }
            sb.append("}");
            if(showCompleteSQL){
                sb.append("\nCompleteSQL : ").append(CreateSql.getCompleteSql(sql,obj));
            }
        }
        Console.println(Console.whiteStr(sb.toString()));
    }

    //打印批量处理的SQL
    private void logBatch(String sql,Object obj[][]) {
        StringBuilder sb=new StringBuilder(  "\nTime       : ").append(BaseUtils.time()).append("\nDatabase   : ").append("<"+dbname+"> ");
        sb.append(jdbcUrl).append("\n").append("SQL        : ").append(formatSql(sql));
        if(obj==null||obj.length==0) {
            sb.append("\nParameters : { }");
        } else {
            for(int i=0;i<obj.length;i++) {
                sb.append("\nParameters : { ");
                for(Object o:obj[i]) {
                    sb.append("(").append((o!=null?o.getClass().getSimpleName():"NULL")).append(")").append(o).append("   ");
                }
                sb.append("}");
            }
        }
        Console.println(Console.whiteStr(sb.toString()));
    }

    private void logBatch(String[] sqls){
        StringBuilder sqlB=new StringBuilder();
        for (String sql : sqls) {
            sqlB.append(sql).append("\n");
        }
        StringBuilder sb=new StringBuilder(  "\nTime       : ").append(BaseUtils.time()).append("\nDatabase   : ").append("<"+dbname+"> ").append(jdbcUrl).append("\n");
        sb.append("SQL        : ").append("\n");
        sb.append(sqlB.toString());
        Console.println(Console.whiteStr(sb.toString()));
    }


    //打印格式化后的SQL
    private String formatSql(String sql) {
        if(isFormatSqlLog) {
            return "\n"+sqlFormatUtil.format(sql);
        }
        return sql;

    }

}
