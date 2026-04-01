package org.database.concurrency;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws SQLException, InterruptedException {
        var props = new Properties();
        props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        props.setProperty("dataSource.serverName", "localhost");
        props.setProperty("dataSource.portNumber", "5432");
        props.setProperty("dataSource.serverName", "localhost");
        props.setProperty("dataSource.user", "postgres");
        props.setProperty("dataSource.password", "admin");
        props.setProperty("dataSource.databaseName", "counter");
        props.put("dataSource.logWriter", new PrintWriter(System.out));
        props.setProperty("transactionIsolation", "TRANSACTION_SERIALIZABLE");

        HikariConnectionProvider.setConfig(props);

        HikariConnectionProvider connectionProvider = HikariConnectionProvider.getInstance();

        ExecutorService executorService = Executors.newFixedThreadPool(12);

        var txm = new TransactionManager(connectionProvider);
        var dao = new CounterDao(txm);
        var cdl = new CountDownLatch(10_000);
        for(int i = 0; i < 10_000; i++) {
            executorService.execute(() -> {
                try {
                    boolean success = false;
                    while (!success) {
                        try {
                            txm.beginTransaction();
                            Counter c = dao.getCounter();
                            c.increment();
                            dao.updateCounter(c.value());
                            txm.commit();
                            success = true;
                        } catch (SQLException e) {
                            txm.rollback();
                            if ("40001".equals(e.getSQLState())) { // serialization failure
                                // retry
                            } else {
                                throw e;
                            }
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    cdl.countDown();
                }
            });

        }
        cdl.await();
        executorService.shutdown();
        System.out.println(dao.getCounter().value());
        dao.updateCounter(0);
    }
}