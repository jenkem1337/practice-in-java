package org.ConnectionPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private final DataSource dataSource;
    private static String ALL_USERS =  "select * from users";
    private UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public static UserDao hikariCp(){
        final var dataSourceProvider = HikariDataSourceProvider.getInstance();
        return new UserDao(dataSourceProvider.getDataSource());
    }

    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try(Connection connection = dataSource.getConnection()) {
            System.out.println("Thread: " + Thread.currentThread().getName() + " got connection: " + connection);

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(ALL_USERS);
            while(rs.next()) {
                var user = new User(
                        rs.getLong(1),
                        rs.getString(2)
                );
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
}
