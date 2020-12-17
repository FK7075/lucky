package com.lucky.jacklamb.datasource;

import com.lucky.jacklamb.exception.NoDataSourceException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * LUCKY数据源抽象
 * 1.每个数据源要有一个唯一的标识_[dbname]
 * 2.数据源要提供数据库链接操作相关的API_[获取链接，关闭资源等]
 * @author fk
 * @version 1.0
 * @date 2020/12/17 0017 9:35
 */
public abstract class LuckyDataSource {

    //数据源的唯一标识
    private String dbname;
    //数据库地址
    private String jdbcUrl;
    //登录名
    private String username;
    //登录密码
    private String password;
    //驱动的全限定名
    private String driverClass;
    //数据源
    private DataSource dataSource;

    private Boolean log;
    private Boolean showCompleteSQL;
    private Boolean cache;
    private String cacheType;
    private String cacheExpiredTime;
    private Integer cacheCapacity;
    private Boolean formatSqlLog;
    private Set<Class<?>> createTable;
    private String reversePack;
    private String srcPath;

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Boolean getLog() {
        return log;
    }

    public void setLog(Boolean log) {
        this.log = log;
    }

    public Boolean getShowCompleteSQL() {
        return showCompleteSQL;
    }

    public void setShowCompleteSQL(Boolean showCompleteSQL) {
        this.showCompleteSQL = showCompleteSQL;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public String getCacheType() {
        return cacheType;
    }

    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public String getCacheExpiredTime() {
        return cacheExpiredTime;
    }

    public void setCacheExpiredTime(String cacheExpiredTime) {
        this.cacheExpiredTime = cacheExpiredTime;
    }

    public Integer getCacheCapacity() {
        return cacheCapacity;
    }

    public void setCacheCapacity(Integer cacheCapacity) {
        this.cacheCapacity = cacheCapacity;
    }

    public Boolean getFormatSqlLog() {
        return formatSqlLog;
    }

    public void setFormatSqlLog(Boolean formatSqlLog) {
        this.formatSqlLog = formatSqlLog;
    }

    public Set<Class<?>> getCreateTable() {
        return createTable;
    }

    public void setCreateTable(Set<Class<?>> createTable) {
        this.createTable = createTable;
    }

    public String getReversePack() {
        return reversePack;
    }

    public void setReversePack(String reversePack) {
        this.reversePack = reversePack;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public LuckyDataSource(){
        createTable= new HashSet<>();
        dbname="defaultDB";
        cache=true;
        cacheType="Java";
        cacheExpiredTime="0";
        log=false;
        showCompleteSQL=false;
        formatSqlLog=false;
        cacheCapacity=50;
    }

    public Connection getConnection(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new NoDataSourceException(e);
        }
    }

    /**
     * 关闭数据库资源
     * @param rs ResultSet对象
     * @param ps PreparedStatement对象
     * @param conn Connection对象
     */
    public static void close(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("关闭数据库资源错误！");
        }
    }
}
