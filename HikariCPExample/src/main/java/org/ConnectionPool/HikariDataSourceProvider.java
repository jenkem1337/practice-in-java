package org.ConnectionPool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Properties;

public class HikariDataSourceProvider {
    private final HikariDataSource dataSource;
    public final static int MIN_POOL_SIZE = Runtime.getRuntime().availableProcessors() / 2;
    public final static int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private HikariDataSourceProvider() {
        final var props = new Properties();
        props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        props.setProperty("dataSource.serverName", "localhost");
        props.setProperty("dataSource.portNumber", "5432");
        props.setProperty("dataSource.serverName", "localhost");
        props.setProperty("dataSource.user", "postgres");
        props.setProperty("dataSource.password", "admin");
        props.setProperty("dataSource.databaseName", "hikari-cp-example");
        props.put("dataSource.logWriter", new PrintWriter(System.out));
        dataSource = new HikariDataSource(
                new HikariConfig(props)
        );
        dataSource.setMaximumPoolSize(MIN_POOL_SIZE);
        dataSource.setMaximumPoolSize(MAX_POOL_SIZE);
        dataSource.setIdleTimeout(1000 * 30);
        dataSource.setConnectionTimeout(1000);
        dataSource.setLeakDetectionThreshold(1000);
    }
    private static class SingletonHolder {
        private static HikariDataSourceProvider INSTANCE = new HikariDataSourceProvider();
    }

    public static HikariDataSourceProvider getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }
}
