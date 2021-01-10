package com.lucky.datasource.sql;

import com.lucky.utils.config.Value;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/18 0018 9:52
 */
public class HikariCPDataSource extends LuckyDataSource {

    private HikariDataSource hikariDataSource;

    @Value("data-source-class-name")
    private String dataSourceClassName;
    @Value("auto-commit")
    private Boolean autoCommit;
    @Value("connection-timeout")
    private Integer connectionTimeout;
    @Value("idle-timeout")
    private Integer idleTimeout;
    @Value("max-life-time")
    private Integer maxLifetime;
    @Value("connection-test-query")
    private String connectionTestQuery;
    @Value("minimum-idle")
    private Integer minimumIdle;
    @Value("maximum-pool-size")
    private Integer maximumPoolSize;
    @Value("pool-name")
    private String poolName;
    @Value("metric-registry")
    private Object metricRegistry;
    @Value("health-check-registry")
    private Properties healthCheckRegistry;
    @Value("initialization-fail-timeout")
    private Integer initializationFailTimeout;
    @Value("isolate-internal-queries")
    private Boolean isolateInternalQueries;
    @Value("allow-pool-suspension")
    private Boolean allowPoolSuspension;
    @Value("read-only")
    private Boolean readOnly;
    @Value("register-mbeans")
    private Boolean registerMbeans;
    @Value("catalog")
    private String catalog;
    @Value("connection-init-sql")
    private String connectionInitSql;
    @Value("transaction-isolation")
    private String transactionIsolation;
    @Value("validation-timeout")
    private Integer validationTimeout;
    @Value("leak-detection-threshold")
    private Integer leakDetectionThreshold;
    @Value("data-source")
    private DataSource dataSource;
    @Value("schema")
    private String schema;
    @Value("thread-factory")
    private ThreadFactory threadFactory;
    @Value("scheduled-executor-service")
    private ScheduledExecutorService scheduledExecutorService;

    public void setDataSourceClassName(String dataSourceClassName) {
        this.dataSourceClassName = dataSourceClassName;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setIdleTimeout(Integer idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public void setMaxLifetime(Integer maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public void setConnectionTestQuery(String connectionTestQuery) {
        this.connectionTestQuery = connectionTestQuery;
    }

    public void setMinimumIdle(Integer minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public void setMaximumPoolSize(Integer maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public void setMetricRegistry(Object metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void setHealthCheckRegistry(Properties healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
    }

    public void setInitializationFailTimeout(Integer initializationFailTimeout) {
        this.initializationFailTimeout = initializationFailTimeout;
    }

    public void setIsolateInternalQueries(Boolean isolateInternalQueries) {
        this.isolateInternalQueries = isolateInternalQueries;
    }

    public void setAllowPoolSuspension(Boolean allowPoolSuspension) {
        this.allowPoolSuspension = allowPoolSuspension;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setRegisterMbeans(Boolean registerMbeans) {
        this.registerMbeans = registerMbeans;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public void setConnectionInitSql(String connectionInitSql) {
        this.connectionInitSql = connectionInitSql;
    }

    public void setTransactionIsolation(String transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }

    public void setValidationTimeout(Integer validationTimeout) {
        this.validationTimeout = validationTimeout;
    }

    public void setLeakDetectionThreshold(Integer leakDetectionThreshold) {
        this.leakDetectionThreshold = leakDetectionThreshold;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public HikariCPDataSource() {
        super();
        connectionTestQuery = "SELECT 1";
        autoCommit = true;
        connectionTimeout = 30000;
        idleTimeout = 600000;
        maxLifetime = 1800000;
        maximumPoolSize = 10;
        minimumIdle = maximumPoolSize;
        initializationFailTimeout = 1;
        isolateInternalQueries = false;
        allowPoolSuspension = false;
        readOnly = false;
        registerMbeans = false;
        validationTimeout = 5000;
        leakDetectionThreshold = 0;
    }

    @Override
    public String poolType() {
        return "HikariCP";
    }

    @Override
    public DataSource createDataSource() {
        if(hikariDataSource==null){
            hikariDataSource= new HikariDataSource(createConfig());
        }
        return hikariDataSource;
    }

    protected HikariConfig createConfig(){
        HikariConfig hikariCfg = new HikariConfig();
        hikariCfg.setDriverClassName(getDriverClass());
        hikariCfg.setJdbcUrl(getJdbcUrl());
        hikariCfg.setUsername(getUsername());
        hikariCfg.setPassword(getPassword());
        hikariCfg.setAutoCommit(autoCommit);
        hikariCfg.setConnectionTimeout(connectionTimeout);
        hikariCfg.setIdleTimeout(idleTimeout);
        hikariCfg.setMaxLifetime(maxLifetime);
        hikariCfg.setConnectionTestQuery(connectionTestQuery);//Object
        hikariCfg.setMinimumIdle(minimumIdle);
        hikariCfg.setMaximumPoolSize(maximumPoolSize);
        hikariCfg.setMetricRegistry(metricRegistry);//Object
        if (healthCheckRegistry != null)
            hikariCfg.setHealthCheckProperties(healthCheckRegistry);//Object
        if (poolName != null)
            hikariCfg.setPoolName(poolName);
        hikariCfg.setIsolateInternalQueries(isolateInternalQueries);
        hikariCfg.setAllowPoolSuspension(allowPoolSuspension);
        hikariCfg.setReadOnly(readOnly);
        hikariCfg.setRegisterMbeans(registerMbeans);
        hikariCfg.setInitializationFailTimeout(initializationFailTimeout);
        hikariCfg.setConnectionInitSql(connectionInitSql);
        hikariCfg.setLeakDetectionThreshold(leakDetectionThreshold);
        hikariCfg.setDataSource(dataSource);
        hikariCfg.setSchema(schema);
        hikariCfg.setCatalog(catalog);
        hikariCfg.setValidationTimeout(validationTimeout);
        hikariCfg.setTransactionIsolation(transactionIsolation);
        hikariCfg.setThreadFactory(threadFactory);
        hikariCfg.setScheduledExecutor(scheduledExecutorService);
        return hikariCfg;
    }

    @Override
    public void destroy() {
        if(hikariDataSource!=null){
            hikariDataSource.close();
        }
    }
}
