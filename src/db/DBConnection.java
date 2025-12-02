package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USER = "course_registration";
    private static final String PASSWORD = "oracle";


    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("[OK] Oracle JDBC Driver Loaded!");
        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR] Failed to load Oracle JDBC Driver");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to connect to DB");
            e.printStackTrace();
            return null;
        }
    }
}
