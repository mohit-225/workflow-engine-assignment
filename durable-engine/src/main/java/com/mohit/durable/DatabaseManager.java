package com.mohit.durable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:h2:./durable_db";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try {
            initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    private static void initializeDatabase() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String createTableSQL = """
                    CREATE TABLE IF NOT EXISTS steps (
                        workflow_id VARCHAR(255),
                        step_name VARCHAR(255),
                        sequence_number INT,
                        result CLOB,
                        PRIMARY KEY (workflow_id, sequence_number)
                    )
                    """;

            statement.execute(createTableSQL);
        }
    }
}
