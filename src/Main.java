import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Database URL
        String url = "jdbc:mysql://localhost:3306/Hospital";

        // Database credentials
        String username = "root";
        String password = "root";

        // Establish the connection
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to the database.");
            // Perform database operations here
        } catch (SQLException e) {
            System.err.println("Connection Failed: " + e.getMessage());
        }
    }
}
