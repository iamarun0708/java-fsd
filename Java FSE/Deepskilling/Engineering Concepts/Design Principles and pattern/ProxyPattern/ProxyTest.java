package ProxyPattern;

/**
 * Exercise 6: Implementing the Proxy Pattern
 * 
 * Scenario: An image viewer that loads images from a remote server.
 * Proxy adds lazy initialization and caching.
 */

// --- Subject Interface ---
interface Image {
    void display();
}

// --- Real Subject ---
class RealImage implements Image {
    private String filename;

    public RealImage(String filename) {
        this.filename = filename;
        loadFromServer();  // Expensive operation
    }

    private void loadFromServer() {
        System.out.println("  Loading image from remote server: " + filename);
        // Simulate network delay
        try { Thread.sleep(500); } catch (InterruptedException e) { }
        System.out.println("  Image loaded: " + filename);
    }

    @Override
    public void display() {
        System.out.println("  Displaying image: " + filename);
    }
}

// --- Proxy Class ---
class ProxyImage implements Image {
    private String filename;
    private RealImage realImage;  // Cached reference

    public ProxyImage(String filename) {
        this.filename = filename;
        // Note: RealImage is NOT created here (lazy initialization)
    }

    @Override
    public void display() {
        if (realImage == null) {
            // Lazy loading: create RealImage only when first needed
            System.out.println("  [Proxy] First access - loading image...");
            realImage = new RealImage(filename);
        } else {
            System.out.println("  [Proxy] Using cached image...");
        }
        realImage.display();
    }
}

// --- Test Class ---
public class ProxyTest {
    public static void main(String[] args) {
        System.out.println("=== Proxy Pattern Demo ===\n");

        // Create proxy images (no loading happens yet)
        Image image1 = new ProxyImage("photo_landscape.jpg");
        Image image2 = new ProxyImage("photo_portrait.jpg");

        System.out.println("Images created. No loading has happened yet!\n");

        // First access - triggers lazy loading
        System.out.println("--- First display of image1 ---");
        image1.display();

        // Second access - uses cached version
        System.out.println("\n--- Second display of image1 (cached) ---");
        image1.display();

        // First access of image2
        System.out.println("\n--- First display of image2 ---");
        image2.display();

        System.out.println("\n✓ Proxy provides lazy loading and caching!");
    }
}
