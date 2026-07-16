package LibraryManagement;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Exercise 6: Library Management System
 * 
 * Search for books by title using Linear Search and Binary Search.
 */

// --- Book Class ---
class Book {
    private int bookId;
    private String title;
    private String author;

    public Book(int bookId, String title, String author) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }

    @Override
    public String toString() {
        return String.format("  ID:%-4d | %-30s | %s", bookId, title, author);
    }
}

// --- Library with Search ---
public class LibraryTest {

    /**
     * Linear Search by Title - O(n)
     * Works on unsorted data.
     */
    public static Book linearSearchByTitle(Book[] books, String title) {
        int comparisons = 0;
        for (Book book : books) {
            comparisons++;
            if (book.getTitle().equalsIgnoreCase(title)) {
                System.out.println("    [Linear] Found in " + comparisons + " comparisons");
                return book;
            }
        }
        System.out.println("    [Linear] Not found after " + comparisons + " comparisons");
        return null;
    }

    /**
     * Binary Search by Title - O(log n)
     * Requires sorted array by title.
     */
    public static Book binarySearchByTitle(Book[] sortedBooks, String title) {
        int low = 0, high = sortedBooks.length - 1;
        int comparisons = 0;

        while (low <= high) {
            comparisons++;
            int mid = low + (high - low) / 2;
            int cmp = sortedBooks[mid].getTitle().compareToIgnoreCase(title);

            if (cmp == 0) {
                System.out.println("    [Binary] Found in " + comparisons + " comparisons");
                return sortedBooks[mid];
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        System.out.println("    [Binary] Not found after " + comparisons + " comparisons");
        return null;
    }

    public static void main(String[] args) {
        System.out.println("=== Exercise 6: Library Management System ===\n");

        /*
         * When to use each search:
         * 
         * Linear Search:
         * - Small datasets (< 50 items)
         * - Unsorted data
         * - One-time searches (not worth sorting first)
         * 
         * Binary Search:
         * - Large datasets
         * - Sorted data (or worth sorting for repeated searches)
         * - Frequent lookups on the same data
         */

        Book[] books = {
            new Book(1, "Data Structures in Java", "Robert Lafore"),
            new Book(2, "Algorithms", "Robert Sedgewick"),
            new Book(3, "Clean Code", "Robert C. Martin"),
            new Book(4, "Design Patterns", "Gang of Four"),
            new Book(5, "Effective Java", "Joshua Bloch"),
            new Book(6, "Head First Java", "Kathy Sierra"),
            new Book(7, "Java Concurrency", "Brian Goetz"),
            new Book(8, "Java Performance", "Scott Oaks"),
            new Book(9, "Refactoring", "Martin Fowler"),
            new Book(10, "The Pragmatic Programmer", "David Thomas"),
        };

        System.out.println("Books in library:");
        for (Book b : books) {
            System.out.println(b);
        }

        // Sort by title for binary search
        Book[] sortedBooks = Arrays.copyOf(books, books.length);
        Arrays.sort(sortedBooks, Comparator.comparing(b -> b.getTitle().toLowerCase()));

        // Search: existing book
        String searchTitle = "Effective Java";
        System.out.println("\n--- Searching for: \"" + searchTitle + "\" ---");
        Book result1 = linearSearchByTitle(books, searchTitle);
        Book result2 = binarySearchByTitle(sortedBooks, searchTitle);
        System.out.println("  Result: " + (result1 != null ? result1 : "Not found"));

        // Search: another book at end
        searchTitle = "The Pragmatic Programmer";
        System.out.println("\n--- Searching for: \"" + searchTitle + "\" ---");
        linearSearchByTitle(books, searchTitle);
        binarySearchByTitle(sortedBooks, searchTitle);

        // Search: non-existent
        searchTitle = "Introduction to Algorithms";
        System.out.println("\n--- Searching for: \"" + searchTitle + "\" (not in library) ---");
        linearSearchByTitle(books, searchTitle);
        binarySearchByTitle(sortedBooks, searchTitle);

        System.out.println("\n✓ For " + books.length + " books:");
        System.out.println("  Linear Search: up to " + books.length + " comparisons");
        System.out.println("  Binary Search: up to " + (int)(Math.log(books.length)/Math.log(2) + 1) + " comparisons");
    }
}
