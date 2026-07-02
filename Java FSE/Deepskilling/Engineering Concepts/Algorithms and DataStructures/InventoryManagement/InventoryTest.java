package InventoryManagement;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Exercise 1: Inventory Management System
 * 
 * Demonstrates why data structures are essential for large inventories.
 * Uses ArrayList and HashMap for storage and retrieval.
 */

// --- Product Class ---
class Product {
    private int productId;
    private String productName;
    private int quantity;
    private double price;

    public Product(int productId, String productName, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("  ID:%-4d | %-20s | Qty: %-5d | $%.2f", productId, productName, quantity, price);
    }
}

// --- Inventory using ArrayList ---
class InventoryArrayList {
    private ArrayList<Product> products = new ArrayList<>();

    // Add - O(1) amortized
    public void addProduct(Product product) {
        products.add(product);
        System.out.println("  [ArrayList] Added: " + product.getProductName());
    }

    // Search by ID - O(n)
    public Product findProduct(int productId) {
        for (Product p : products) {
            if (p.getProductId() == productId) return p;
        }
        return null;
    }

    // Update - O(n) search + O(1) update
    public void updateProduct(int productId, int newQuantity, double newPrice) {
        Product p = findProduct(productId);
        if (p != null) {
            p.setQuantity(newQuantity);
            p.setPrice(newPrice);
            System.out.println("  [ArrayList] Updated: " + p.getProductName());
        } else {
            System.out.println("  [ArrayList] Product not found: ID " + productId);
        }
    }

    // Delete - O(n)
    public void deleteProduct(int productId) {
        Product p = findProduct(productId);
        if (p != null) {
            products.remove(p);
            System.out.println("  [ArrayList] Deleted: " + p.getProductName());
        }
    }

    public void displayAll() {
        System.out.println("  --- ArrayList Inventory ---");
        for (Product p : products) {
            System.out.println(p);
        }
    }
}

// --- Inventory using HashMap (optimized for lookups) ---
class InventoryHashMap {
    private HashMap<Integer, Product> products = new HashMap<>();

    // Add - O(1)
    public void addProduct(Product product) {
        products.put(product.getProductId(), product);
        System.out.println("  [HashMap] Added: " + product.getProductName());
    }

    // Search by ID - O(1)
    public Product findProduct(int productId) {
        return products.get(productId);
    }

    // Update - O(1)
    public void updateProduct(int productId, int newQuantity, double newPrice) {
        Product p = products.get(productId);
        if (p != null) {
            p.setQuantity(newQuantity);
            p.setPrice(newPrice);
            System.out.println("  [HashMap] Updated: " + p.getProductName());
        } else {
            System.out.println("  [HashMap] Product not found: ID " + productId);
        }
    }

    // Delete - O(1)
    public void deleteProduct(int productId) {
        Product p = products.remove(productId);
        if (p != null) {
            System.out.println("  [HashMap] Deleted: " + p.getProductName());
        }
    }

    public void displayAll() {
        System.out.println("  --- HashMap Inventory ---");
        for (Product p : products.values()) {
            System.out.println(p);
        }
    }
}

// --- Test Class ---
public class InventoryTest {
    public static void main(String[] args) {
        System.out.println("=== Exercise 1: Inventory Management System ===\n");

        /*
         * Time Complexity Analysis:
         * 
         * Operation    | ArrayList | HashMap
         * -------------|-----------|--------
         * Add          | O(1)*     | O(1)
         * Search by ID | O(n)      | O(1)
         * Update       | O(n)      | O(1)
         * Delete       | O(n)      | O(1)
         * 
         * * amortized - may be O(n) when resizing
         * 
         * HashMap is better when frequent lookups by ID are needed.
         * ArrayList is better when order matters or index-based access is needed.
         */

        // --- Using ArrayList ---
        System.out.println("--- ArrayList Implementation ---");
        InventoryArrayList arrayInventory = new InventoryArrayList();
        arrayInventory.addProduct(new Product(101, "Laptop", 50, 999.99));
        arrayInventory.addProduct(new Product(102, "Mouse", 200, 29.99));
        arrayInventory.addProduct(new Product(103, "Keyboard", 150, 49.99));
        arrayInventory.displayAll();

        System.out.println("\n  Updating product 102...");
        arrayInventory.updateProduct(102, 180, 24.99);

        System.out.println("  Deleting product 103...");
        arrayInventory.deleteProduct(103);
        arrayInventory.displayAll();

        // --- Using HashMap ---
        System.out.println("\n--- HashMap Implementation ---");
        InventoryHashMap hashInventory = new InventoryHashMap();
        hashInventory.addProduct(new Product(201, "Monitor", 75, 299.99));
        hashInventory.addProduct(new Product(202, "Headset", 120, 79.99));
        hashInventory.addProduct(new Product(203, "Webcam", 90, 59.99));
        hashInventory.displayAll();

        System.out.println("\n  Searching for product 202...");
        Product found = hashInventory.findProduct(202);
        System.out.println("  Found: " + (found != null ? found : "Not found"));

        System.out.println("\n✓ HashMap provides O(1) for all operations by ID!");
    }
}
