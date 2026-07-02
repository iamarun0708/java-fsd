package SingletonPattern;

/**
 * Test class for Singleton Pattern.
 * Verifies that only one instance of Logger is created.
 */
public class SingletonTest {
    public static void main(String[] args) {
        System.out.println("=== Singleton Pattern Demo ===\n");

        // Get two references to the Logger
        Logger logger1 = Logger.getInstance();
        Logger logger2 = Logger.getInstance();

        // Verify both references point to the same object
        System.out.println("logger1 hashCode: " + logger1.hashCode());
        System.out.println("logger2 hashCode: " + logger2.hashCode());
        System.out.println("Are both instances the same? " + (logger1 == logger2));

        // Use the logger
        logger1.log("Application started");
        logger2.log("Processing request");
        logger1.warn("Low memory");
        logger2.error("Connection failed");

        System.out.println("\n✓ Both references point to the SAME Logger instance!");
    }
}
