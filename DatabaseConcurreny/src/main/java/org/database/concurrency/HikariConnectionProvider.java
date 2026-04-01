package org.database.concurrency;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class HikariConnectionProvider implements ConnectionProvider {
    private final HikariDataSource dataSource;
    private volatile boolean closed = false;
    private static Properties CONFIG;
    public final static int MIN_POOL_SIZE = Runtime.getRuntime().availableProcessors() / 2;
    public final static int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static class SingletonHolder {
        private static HikariConnectionProvider INSTANCE = new HikariConnectionProvider();
    }

    public HikariConnectionProvider() {
        this.dataSource = new HikariDataSource(new HikariConfig(CONFIG));
        dataSource.setMaximumPoolSize(MIN_POOL_SIZE);
        dataSource.setMaximumPoolSize(MAX_POOL_SIZE);
        dataSource.setIdleTimeout(1000 * 30);
        dataSource.setConnectionTimeout(1000);
        dataSource.setLeakDetectionThreshold(1000);
    }

    public static void setConfig(Properties properties){
        CONFIG = properties;
    }
    public static HikariConnectionProvider getInstance(){
        return SingletonHolder.INSTANCE;
    }
    @Override
    public Connection getConnection() throws SQLException {
        if (closed) throw new IllegalStateException("ConnectionProvider already closed");
        return dataSource.getConnection();
    }

    @Override
    public void close() {
        if (!closed && !dataSource.isClosed()) {
            closed = true;
            dataSource.close();
        }
    }
}
