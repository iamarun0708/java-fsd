import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class JDBCTransactionDemo {
    private static final String URL = "jdbc:sqlite:bank.db";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (id INTEGER PRIMARY KEY, balance DOUBLE)");
            stmt.execute("INSERT OR IGNORE INTO accounts (id, balance) VALUES (1, 1000.0), (2, 500.0)");
            
        } catch (Exception e) {
            // Setup ignored
        }

        transferMoney(1, 2, 200.0);
    }

    public static void transferMoney(int fromAcc, int toAcc, double amount) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);

            try (PreparedStatement withdraw = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
                 PreparedStatement deposit = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?")) {
                
                withdraw.setDouble(1, amount);
                withdraw.setInt(2, fromAcc);
                withdraw.executeUpdate();

                deposit.setDouble(1, amount);
                deposit.setInt(2, toAcc);
                deposit.executeUpdate();

                conn.commit();
                System.out.println("Transaction committed successfully.");
            } catch (Exception e) {
                conn.rollback();
                System.out.println("Transaction rolled back due to error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}
