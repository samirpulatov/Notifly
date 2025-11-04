package org.notifly.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager initializes the SQLite database and provides a helper to get JDBC connections.
 */
public class DatabaseManager {

    // JDBC URL for SQLite. The file 'notifly.db' will be created in the working directory (next to the JAR).
    private static final String DB_URL = "jdbc:sqlite:notifly.db";

    /*
     * Static initializer: runs once when the class is loaded.
     * It creates necessary tables if they do not exist yet.
     * Using try-with-resources to ensure Connection and Statement are closed automatically.
     */
    static {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Create users table: stores known chat IDs (unique)
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    chat_id LONG UNIQUE
                );
            """;

            // Create reminders table: stores reminders linked to chat_id
            String createRemindersTable = """
                CREATE TABLE IF NOT EXISTS reminders (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    chat_id LONG,
                    date TEXT NOT NULL,
                    description TEXT,
                    FOREIGN KEY (chat_id) REFERENCES users(chat_id)
                );
            """;

            // Execute DDL statements
            stmt.execute(createUsersTable);
            stmt.execute(createRemindersTable);

            // Simple feedback to console that DB is ready
            System.out.println("✅ Database initialized successfully");
        } catch (SQLException e) {
            // Print stack trace so developer can see initialization errors
            e.printStackTrace();
        }
    }

    /**
     * Returns a new JDBC Connection to the SQLite database.
     * Caller is responsible for closing the connection (prefer try-with-resources).
     *
     * @return Connection to notifly.db
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
