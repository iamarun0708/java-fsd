import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JDBCConnectionDemo {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:students.db";
        
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {
                
                stmt.execute("CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY, name TEXT)");
                stmt.execute("INSERT OR IGNORE INTO students (id, name) VALUES (1, 'Alice'), (2, 'Bob')");
                
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
                    while (rs.next()) {
                        System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}
