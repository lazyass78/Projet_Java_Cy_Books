package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections.
 *
 * This class provides a method to establish a connection to the database using JDBC.
 * The database URL, user, and password are defined as constants.
 *
 */
public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/Library";
    private static final String USER = "root";
    private static final String PASSWORD = "cytech0001";

    /**
     * Establishes a connection to the database.
     * This method attempts to create and return a connection to the database using the
     * specified URL, user, and password.
     *
     * @return A {@link Connection} object to interact with the database.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
