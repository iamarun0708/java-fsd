import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

class StudentDAO {
    private String url = "jdbc:sqlite:students.db";

    public void insertStudent(int id, String name) {
        String sql = "INSERT OR IGNORE INTO students(id, name) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
            System.out.println("Inserted student successfully.");
        } catch (Exception e) {
            System.out.println("Insert failed: " + e.getMessage());
        }
    }

    public void updateStudentName(int id, String name) {
        String sql = "UPDATE students SET name = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Updated student successfully.");
        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }
}

public class JDBCInsertUpdateDemo {
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:students.db");
                 Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY, name TEXT)");
            }
        } catch (Exception e) {
            // Ignored for SQLite initialization setup
        }

        StudentDAO dao = new StudentDAO();
        dao.insertStudent(3, "Charlie");
        dao.updateStudentName(3, "Charles");
    }
}
