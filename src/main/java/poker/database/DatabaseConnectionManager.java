package poker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static final String DB_URL = getConfig(
            "POKER_DB_URL",
            "jdbc:mysql://127.0.0.1:3307/poker_online?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
    private static final String USER = getConfig("POKER_DB_USER", "root");
    private static final String PASSWORD = getConfig("POKER_DB_PASSWORD", "sesame");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    private static String getConfig(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
