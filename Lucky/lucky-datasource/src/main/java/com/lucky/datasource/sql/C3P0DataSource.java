package com.lucky.datasource.sql;

import com.lucky.utils.config.Value;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/8 0008 14:40
 */
public class C3P0DataSource extends LuckyDataSource{

    private static final Logger log= LoggerFactory.getLogger(C3P0DataSource.class);
    private ComboPooledDataSource dataSource;

    @Value("acquire-increment")
    private Integer acquireIncrement;
    @Value("initial-pool-size")
    private Integer initialPoolSize;
    @Value("max-pool-size")
    private Integer maxPoolSize;
    @Value("min-pool-size")
    private Integer minPoolSize;
    @Value("max-idle-time")
    private Integer maxidleTime;
    @Value("max-connection-age")
    private Integer maxConnectionAge;
    @Value("max-statements")
    private Integer maxStatements;
    @Value("max-statements-per-connection")
    private Integer maxStatementsPerConnection;
    @Value("checkout-timeout")
    private Integer checkoutTimeout;

    public int getAcquireIncrement() {
        return acquireIncrement;
    }

    /**
     * 连接池在无空闲连接可用时一次性创建的新数据库连接数,default : 3
     * @param acquireIncrement
     */
    public void setAcquireIncrement(int acquireIncrement) {
        this.acquireIncrement = acquireIncrement;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    /**
     * 连接池初始化时创建的连接数,default : 3，取值应在minPoolSize与maxPoolSize之间
     * @param initialPoolSize
     */
    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * 连接池中拥有的最大连接数，如果获得新连接时会使连接总数超过这个值则不会再获取新连接，而是等待其他连接释放 default : 15
     * @param maxPoolSize
     */
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    /**
     * 连接池保持的最小连接数,default : 3
     * @param minPoolSize
     */
    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMaxidleTime() {
        return maxidleTime;
    }

    /**
     * 连接的最大空闲时间，如果超过这个时间，某个数据库连接还没有被使用，则会断开掉这个连接。如果为0，则永远不会断开连接,即回收此连接。default : 0s
     * @param maxidleTime
     */
    public void setMaxidleTime(int maxidleTime) {
        this.maxidleTime = maxidleTime;
    }

    public int getMaxConnectionAge() {
        return maxConnectionAge;
    }

    /**
     * 这个配置主要时为了减轻连接池的负载，配置不为 0 则会将连接池中的连接数量保持到minPoolSize，为 0 则不处理
     * @param maxConnectionAge
     */
    public void setMaxConnectionAge(int maxConnectionAge) {
        this.maxConnectionAge = maxConnectionAge;
    }

    public int getMaxStatements() {
        return maxStatements;
    }

    /**
     * JDBC的标准参数，用以控制数据源内加载的PreparedStatements数量。
     * 但由于预缓存的statements属于单个connection而不是整个连接池。所以设置这个参数需要考虑到多方面的因素。
     * 果maxStatements与maxStatementsPerConnection均为0，则缓存被关闭。Default: 0
     * @param maxStatements
     */
    public void setMaxStatements(int maxStatements) {
        this.maxStatements = maxStatements;
    }

    public int getMaxStatementsPerConnection() {
        return maxStatementsPerConnection;
    }


    /**
     * maxStatementsPerConnection定义了连接池内单个连接所拥有的最大缓存statements数。Default: 0
     * @param maxStatementsPerConnection
     */
    public void setMaxStatementsPerConnection(int maxStatementsPerConnection) {
        this.maxStatementsPerConnection = maxStatementsPerConnection;
    }

    public int getCheckoutTimeout() {
        return checkoutTimeout;
    }

    /**
     * 当连接池用完时客户端调用getConnection()后等待获取新连接的时间，超时后将抛出
     * SQLException,如设为0则无限期等待。单位毫秒。Default: 0
     * @param checkoutTimeout
     */
    public void setCheckoutTimeout(int checkoutTimeout) {
        this.checkoutTimeout = checkoutTimeout;
    }

    public C3P0DataSource(){
        super();
        checkoutTimeout=30000;
        acquireIncrement=3;
        initialPoolSize=3;
        minPoolSize=1;
        maxPoolSize=15;
        maxidleTime=0;
        maxConnectionAge=0;
        maxStatements=0;
        maxStatementsPerConnection=0;
    }

    @Override
    public String poolType() {
        return "C3P0";
    }

    @Override
    public DataSource createDataSource() {
        if(dataSource==null){
            dataSource = new ComboPooledDataSource();
            try {
                dataSource.setDriverClass(getDriverClass());
            } catch (PropertyVetoException e) {
                throw new NoDataSourceException("找不到数据库的驱动程序" + getDriverClass());
            }
            dataSource.setJdbcUrl(getJdbcUrl());
            dataSource.setUser(getUsername());
            dataSource.setPassword(getPassword());
            dataSource.setAcquireIncrement(getAcquireIncrement());
            dataSource.setInitialPoolSize(getInitialPoolSize());
            dataSource.setMaxPoolSize(getMaxPoolSize());
            dataSource.setMinPoolSize(getMinPoolSize());
            dataSource.setMaxIdleTime(getMaxidleTime());
            dataSource.setMaxStatements(getMaxStatements());
            dataSource.setMaxConnectionAge(getMaxConnectionAge());
            dataSource.setCheckoutTimeout(getCheckoutTimeout());
            dataSource.setMaxStatementsPerConnection(getMaxStatementsPerConnection());
        }
        return dataSource;
    }

    @Override
    public void destroy() {
        if(dataSource!=null){
            log.info("{} - Shutdown completed",getDbname());
            dataSource.close();
        }
    }
}
