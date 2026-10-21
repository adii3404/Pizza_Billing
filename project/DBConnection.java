
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "pizzacafe";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "root";

    public static Connection getConnection() throws SQLException {
        String dbHost = System.getenv("DB_HOST");
        if (dbHost == null || dbHost.isEmpty()) {
            dbHost = DEFAULT_HOST;
        }
        
        String dbPort = System.getenv("DB_PORT");
        if (dbPort == null || dbPort.isEmpty()) {
            dbPort = DEFAULT_PORT;
        }

        String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + DEFAULT_DB;
        return DriverManager.getConnection(url, DEFAULT_USER, DEFAULT_PASS);
    }
}
