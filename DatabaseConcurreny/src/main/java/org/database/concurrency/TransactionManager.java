package org.database.concurrency;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    private final ConnectionProvider connectionProvider;
    private final static ThreadLocal<Connection> connectionProviderThreadLocal = new ThreadLocal<>();

    public TransactionManager(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void beginTransaction() throws SQLException {
        if (connectionProviderThreadLocal.get() != null) {
            throw new IllegalStateException("Transaction already active on this thread");
        }
        Connection conn = connectionProvider.getConnection();
        conn.setAutoCommit(false);
        connectionProviderThreadLocal.set(conn);
    }

    public Connection getCurrentConnection() throws SQLException {
        Connection conn = connectionProviderThreadLocal.get();
        if (conn == null) {
            return connectionProvider.getConnection();
        }
        return conn;
    }

    public void commit() throws SQLException {
        Connection conn = getCurrentConnection();
        try {
            conn.commit();
        } finally {
            cleanup();
        }
    }

    public void rollback() {
        Connection conn = connectionProviderThreadLocal.get();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cleanup();
            }
        }
    }

    private void cleanup() {
        Connection conn = connectionProviderThreadLocal.get();
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connectionProviderThreadLocal.remove();
            }
        }
    }


}
