package mini_project_dbms;
import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===\n");
        
        // Test 1: Check JDBC Driver
        System.out.println("Test 1: Loading JDBC Driver...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("JDBC Driver loaded successfully!\n");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver NOT found!");
            System.out.println("Error: " + e.getMessage());
            System.out.println("Solution: Add mysql-connector-j-8.2.0.jar to classpath\n");
            return;
        }
        
        // Test 2: Try connection
        String DB_URL = "jdbc:mysql://localhost:3306/Vulcynyx?useSSL=false&allowPublicKeyRetrieval=true";
        String USER = "root";
        String PASS = "Uam@2410";
        
        System.out.println("Test 2: Attempting connection...");
        System.out.println("URL: " + DB_URL);
        System.out.println("User: " + USER);
        System.out.println("Password: " + ("*".repeat(PASS.length())));
        System.out.println();
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("CONNECTION SUCCESSFUL!\n");
            
            // Test 3: Check database
            System.out.println("Test 3: Checking database...");
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("Database: " + metaData.getDatabaseProductName());
            System.out.println("Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver: " + metaData.getDriverName() + " " + metaData.getDriverVersion());
            System.out.println();
            
            // Test 4: List tables
            System.out.println("Test 4: Available tables:");
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            boolean hasTables = false;
            while (tables.next()) {
                hasTables = true;
                System.out.println("  - " + tables.getString("TABLE_NAME"));
            }
            if (!hasTables) {
                System.out.println("No tables found in database!");
            }
            System.out.println();
            
            System.out.println("=== ALL TESTS PASSED ===");
            
        } catch (SQLException e) {
            System.out.println("CONNECTION FAILED!\n");
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
            System.out.println();
            
            // Specific error solutions
            if (e.getMessage().contains("Access denied")) {
                System.out.println("SOLUTION:");
                System.out.println("1. Check username: " + USER);
                System.out.println("2. Check password: Is it correct?");
                System.out.println("3. Try running in MySQL:");
                System.out.println("   ALTER USER 'root'@'localhost' IDENTIFIED BY 'Uam@2410';");
                System.out.println("   FLUSH PRIVILEGES;");
            } else if (e.getMessage().contains("Unknown database")) {
                System.out.println("SOLUTION:");
                System.out.println("1. Create database in MySQL:");
                System.out.println("   CREATE DATABASE Vulcynyx;");
            } else if (e.getMessage().contains("Communications link failure")) {
                System.out.println("SOLUTION:");
                System.out.println("1. MySQL server is not running");
                System.out.println("2. Start MySQL service:");
                System.out.println("   Windows: net start MySQL80");
                System.out.println("   Linux: sudo systemctl start mysql");
            } else if (e.getMessage().contains("No suitable driver")) {
                System.out.println("SOLUTION:");
                System.out.println("1. MySQL Connector JAR is missing from classpath");
                System.out.println("2. Add to compile/run command:");
                System.out.println("   -cp \".;lib\\mysql-connector-j-8.2.0.jar\"");
            }
            
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("\nConnection closed.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
