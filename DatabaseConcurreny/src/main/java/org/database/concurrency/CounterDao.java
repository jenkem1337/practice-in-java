package org.database.concurrency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CounterDao {
    private final TransactionManager transactionManager;

    private static final String SELECT_COUNTER = "SELECT value FROM counter WHERE id = 1";
    private static final String UPDATE_COUNTER = "UPDATE counter SET value = ? WHERE id = 1";
    public CounterDao(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Counter getCounter() throws SQLException {
        Connection connection = transactionManager.getCurrentConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COUNTER);
                ResultSet rs = preparedStatement.executeQuery()) {
            rs.next();
            return Counter.valueOf(rs.getInt("value"));
        }
    }

    public void updateCounter(int value) throws SQLException {
        Connection connection = transactionManager.getCurrentConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_COUNTER)){
            preparedStatement.setInt(1, value);
            preparedStatement.executeUpdate();
        }
    }
}
