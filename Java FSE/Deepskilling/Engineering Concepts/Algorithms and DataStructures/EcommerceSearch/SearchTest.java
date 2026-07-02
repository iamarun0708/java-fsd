package EcommerceSearch;

import java.util.Arrays;

/**
 * Exercise 2: E-commerce Platform Search Function
 * 
 * Implements Linear Search and Binary Search.
 * Compares time complexity: O(n) vs O(log n).
 */

// --- Product Class ---
class Product {
    private int productId;
    private String productName;
    private String category;

    public Product(int productId, String productName, String category) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getCategory() { return category; }

    @Override
    public String toString() {
        return String.format("ID:%-4d | %-20s | %s", productId, productName, category);
    }
}

// --- Search Algorithms ---
public class SearchTest {

    /**
     * Linear Search - O(n)
     * Best case: O(1) - found at first position
     * Average case: O(n/2) = O(n)
     * Worst case: O(n) - found at last position or not found
     */
    public static Product linearSearch(Product[] products, int targetId) {
        int comparisons = 0;
        for (Product p : products) {
            comparisons++;
            if (p.getProductId() == targetId) {
                System.out.println("    Linear Search: Found in " + comparisons + " comparisons");
                return p;
            }
        }
        System.out.println("    Linear Search: Not found after " + comparisons + " comparisons");
        return null;
    }

    /**
     * Binary Search - O(log n)
     * Prerequisite: Array must be sorted by productId
     * Best case: O(1) - found at middle
     * Average case: O(log n)
     * Worst case: O(log n)
     */
    public static Product binarySearch(Product[] sortedProducts, int targetId) {
        int low = 0, high = sortedProducts.length - 1;
        int comparisons = 0;

        while (low <= high) {
            comparisons++;
            int mid = low + (high - low) / 2;  // Avoids integer overflow
            int midId = sortedProducts[mid].getProductId();

            if (midId == targetId) {
                System.out.println("    Binary Search: Found in " + comparisons + " comparisons");
                return sortedProducts[mid];
            } else if (midId < targetId) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        System.out.println("    Binary Search: Not found after " + comparisons + " comparisons");
        return null;
    }

    public static void main(String[] args) {
        System.out.println("=== Exercise 2: E-commerce Search Function ===\n");

        /*
         * Big O Notation Summary:
         * 
         * Algorithm      | Best   | Average  | Worst
         * ---------------|--------|----------|-------
         * Linear Search  | O(1)   | O(n)     | O(n)
         * Binary Search  | O(1)   | O(log n) | O(log n)
         * 
         * Binary Search is much faster but requires sorted data.
         * For an e-commerce platform with thousands of products,
         * Binary Search is preferred when data can be kept sorted.
         */

        // Products array (sorted by ID for binary search)
        Product[] products = {
            new Product(101, "Laptop", "Electronics"),
            new Product(102, "Smartphone", "Electronics"),
            new Product(103, "Headphones", "Accessories"),
            new Product(104, "Tablet", "Electronics"),
            new Product(105, "Keyboard", "Accessories"),
            new Product(106, "Mouse", "Accessories"),
            new Product(107, "Monitor", "Electronics"),
            new Product(108, "Webcam", "Electronics"),
            new Product(109, "Speaker", "Audio"),
            new Product(110, "Printer", "Office"),
        };

        System.out.println("Products in inventory:");
        for (Product p : products) {
            System.out.println("  " + p);
        }

        // Search for product at the end of array (worst case for linear)
        int searchId = 109;
        System.out.println("\n--- Searching for Product ID: " + searchId + " ---");

        Product result1 = linearSearch(products, searchId);
        Product result2 = binarySearch(products, searchId);

        System.out.println("  Result: " + (result1 != null ? result1 : "Not found"));

        // Search for non-existent product
        int missingId = 999;
        System.out.println("\n--- Searching for Product ID: " + missingId + " (not in list) ---");
        linearSearch(products, missingId);
        binarySearch(products, missingId);

        System.out.println("\n✓ Binary Search uses far fewer comparisons for large datasets!");
        System.out.println("  For " + products.length + " items: Linear needs up to " + products.length +
                          " comparisons, Binary needs at most " + (int)(Math.log(products.length)/Math.log(2) + 1));
    }
}
