package com.poker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static final String URL = "jdbc:mysql://localhost:3306/poker_online?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "sesame"; // matching the docker-compose config

    static {
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    /**
     * Gets a new database connection.
     * In a production app, we should use a connection pool (like HikariCP), 
     * but for pure Java core demonstration, we'll use simple DriverManager.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
