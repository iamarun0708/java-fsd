package SingletonPattern;

/**
 * Exercise 1: Implementing the Singleton Pattern
 * 
 * Scenario: A logging utility class with only one instance
 * throughout the application lifecycle for consistent logging.
 */
public class Logger {
    // Eager initialization - instance created at class loading time
    private static Logger instance = new Logger();

    // Private constructor prevents external instantiation
    private Logger() {
        System.out.println("Logger Instance Created");
    }

    // Public static method to get the single instance
    public static Logger getInstance() {
        return instance;
    }

    public void log(String message) {
        System.out.println("[LOG] " + message);
    }

    public void error(String message) {
        System.out.println("[ERROR] " + message);
    }

    public void warn(String message) {
        System.out.println("[WARN] " + message);
    }
}
