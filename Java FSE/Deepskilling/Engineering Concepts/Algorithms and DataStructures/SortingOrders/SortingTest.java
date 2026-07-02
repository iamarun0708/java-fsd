package SortingOrders;

import java.util.Arrays;

/**
 * Exercise 3: Sorting Customer Orders
 * 
 * Implements Bubble Sort and Quick Sort to sort orders by totalPrice.
 * Compares performance: O(n^2) vs O(n log n).
 */

// --- Order Class ---
class Order {
    private int orderId;
    private String customerName;
    private double totalPrice;

    public Order(int orderId, String customerName, double totalPrice) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
    }

    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public double getTotalPrice() { return totalPrice; }

    @Override
    public String toString() {
        return String.format("  Order #%-4d | %-15s | $%,.2f", orderId, customerName, totalPrice);
    }
}

// --- Sorting Algorithms ---
public class SortingTest {

    /**
     * Bubble Sort - O(n^2)
     * Simple but inefficient for large datasets.
     * Compares adjacent elements and swaps if in wrong order.
     */
    public static void bubbleSort(Order[] orders) {
        int n = orders.length;
        int swaps = 0, comparisons = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                comparisons++;
                if (orders[j].getTotalPrice() > orders[j + 1].getTotalPrice()) {
                    // Swap
                    Order temp = orders[j];
                    orders[j] = orders[j + 1];
                    orders[j + 1] = temp;
                    swapped = true;
                    swaps++;
                }
            }
            if (!swapped) break;  // Optimization: stop if no swaps
        }
        System.out.println("  [Bubble Sort] Comparisons: " + comparisons + ", Swaps: " + swaps);
    }

    /**
     * Quick Sort - O(n log n) average, O(n^2) worst case
     * Efficient divide-and-conquer algorithm.
     * Picks a pivot and partitions the array.
     */
    static int quickSortComparisons = 0;

    public static void quickSort(Order[] orders, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(orders, low, high);
            quickSort(orders, low, pivotIndex - 1);
            quickSort(orders, pivotIndex + 1, high);
        }
    }

    private static int partition(Order[] orders, int low, int high) {
        double pivot = orders[high].getTotalPrice();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            quickSortComparisons++;
            if (orders[j].getTotalPrice() <= pivot) {
                i++;
                Order temp = orders[i];
                orders[i] = orders[j];
                orders[j] = temp;
            }
        }

        // Place pivot in correct position
        Order temp = orders[i + 1];
        orders[i + 1] = orders[high];
        orders[high] = temp;

        return i + 1;
    }

    public static void main(String[] args) {
        System.out.println("=== Exercise 3: Sorting Customer Orders ===\n");

        /*
         * Sorting Algorithm Comparison:
         * 
         * Algorithm      | Best       | Average      | Worst       | Space
         * ---------------|------------|--------------|-------------|------
         * Bubble Sort    | O(n)       | O(n^2)       | O(n^2)      | O(1)
         * Insertion Sort | O(n)       | O(n^2)       | O(n^2)      | O(1)
         * Quick Sort     | O(n log n) | O(n log n)   | O(n^2)      | O(log n)
         * Merge Sort     | O(n log n) | O(n log n)   | O(n log n)  | O(n)
         * 
         * Quick Sort is generally preferred because:
         * - Average case is O(n log n)
         * - In-place sorting (low memory usage)
         * - Good cache performance
         */

        Order[] originalOrders = {
            new Order(1001, "Alice", 250.00),
            new Order(1002, "Bob", 89.99),
            new Order(1003, "Charlie", 1250.50),
            new Order(1004, "Diana", 45.00),
            new Order(1005, "Eve", 780.25),
            new Order(1006, "Frank", 150.75),
            new Order(1007, "Grace", 3200.00),
            new Order(1008, "Henry", 99.99),
        };

        // --- Bubble Sort ---
        System.out.println("--- Bubble Sort (by total price) ---");
        Order[] bubbleOrders = Arrays.copyOf(originalOrders, originalOrders.length);
        bubbleSort(bubbleOrders);
        System.out.println("  Sorted orders:");
        for (Order o : bubbleOrders) System.out.println(o);

        // --- Quick Sort ---
        System.out.println("\n--- Quick Sort (by total price) ---");
        Order[] quickOrders = Arrays.copyOf(originalOrders, originalOrders.length);
        quickSortComparisons = 0;
        quickSort(quickOrders, 0, quickOrders.length - 1);
        System.out.println("  [Quick Sort] Comparisons: " + quickSortComparisons);
        System.out.println("  Sorted orders:");
        for (Order o : quickOrders) System.out.println(o);

        System.out.println("\n✓ Quick Sort is generally preferred over Bubble Sort for large datasets!");
    }
}
