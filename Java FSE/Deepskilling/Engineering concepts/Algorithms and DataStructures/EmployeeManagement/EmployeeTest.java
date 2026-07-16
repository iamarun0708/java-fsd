package EmployeeManagement;

/**
 * Exercise 4: Employee Management System
 * 
 * Uses arrays to store and manage employee records.
 * Implements add, search, traverse, and delete operations.
 */

// --- Employee Class ---
class Employee {
    private int employeeId;
    private String name;
    private String position;
    private double salary;

    public Employee(int employeeId, String name, String position, double salary) {
        this.employeeId = employeeId;
        this.name = name;
        this.position = position;
        this.salary = salary;
    }

    public int getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public double getSalary() { return salary; }

    @Override
    public String toString() {
        return String.format("  ID:%-4d | %-15s | %-15s | $%,.2f", employeeId, name, position, salary);
    }
}

// --- Employee Management using Array ---
class EmployeeManager {
    private Employee[] employees;
    private int size;
    private int capacity;

    public EmployeeManager(int capacity) {
        this.capacity = capacity;
        this.employees = new Employee[capacity];
        this.size = 0;
    }

    /**
     * Add employee - O(1) if space available
     */
    public void addEmployee(Employee emp) {
        if (size >= capacity) {
            System.out.println("  Array is full! Cannot add more employees.");
            return;
        }
        employees[size] = emp;
        size++;
        System.out.println("  Added: " + emp.getName());
    }

    /**
     * Search by ID - O(n)
     */
    public Employee searchById(int id) {
        for (int i = 0; i < size; i++) {
            if (employees[i].getEmployeeId() == id) {
                return employees[i];
            }
        }
        return null;
    }

    /**
     * Traverse all - O(n)
     */
    public void traverse() {
        System.out.println("  --- Employee Records (" + size + "/" + capacity + ") ---");
        for (int i = 0; i < size; i++) {
            System.out.println(employees[i]);
        }
    }

    /**
     * Delete by ID - O(n)
     * Shifts elements left to fill the gap
     */
    public boolean deleteById(int id) {
        for (int i = 0; i < size; i++) {
            if (employees[i].getEmployeeId() == id) {
                String name = employees[i].getName();
                // Shift all elements left
                for (int j = i; j < size - 1; j++) {
                    employees[j] = employees[j + 1];
                }
                employees[size - 1] = null;
                size--;
                System.out.println("  Deleted: " + name);
                return true;
            }
        }
        System.out.println("  Employee not found: ID " + id);
        return false;
    }
}

// --- Test Class ---
public class EmployeeTest {
    public static void main(String[] args) {
        System.out.println("=== Exercise 4: Employee Management System ===\n");

        /*
         * Array Representation in Memory:
         * - Contiguous memory block
         * - Fixed size (allocated at creation)
         * - Direct index access is O(1)
         * 
         * Time Complexity:
         *   Add (at end)   : O(1)
         *   Search (by ID) : O(n)
         *   Traverse       : O(n)
         *   Delete         : O(n) - due to shifting
         * 
         * Limitations:
         * - Fixed size - cannot grow dynamically
         * - Deletion requires shifting elements
         * - Insertion in middle is expensive O(n)
         * 
         * When to use arrays:
         * - Known, fixed number of elements
         * - Fast index-based access is needed
         * - Memory efficiency is important
         */

        EmployeeManager manager = new EmployeeManager(5);

        // Add employees
        System.out.println("--- Adding Employees ---");
        manager.addEmployee(new Employee(1, "Arun Kumar", "Developer", 85000));
        manager.addEmployee(new Employee(2, "Priya Sharma", "Designer", 75000));
        manager.addEmployee(new Employee(3, "Rahul Verma", "Manager", 95000));
        manager.addEmployee(new Employee(4, "Sneha Patel", "Tester", 70000));

        // Traverse
        System.out.println("\n--- All Employees ---");
        manager.traverse();

        // Search
        System.out.println("\n--- Searching for Employee ID 3 ---");
        Employee found = manager.searchById(3);
        System.out.println("  Found: " + (found != null ? found : "Not found"));

        // Delete
        System.out.println("\n--- Deleting Employee ID 2 ---");
        manager.deleteById(2);

        System.out.println("\n--- After Deletion ---");
        manager.traverse();

        System.out.println("\n✓ Arrays are efficient for index-based access but have fixed size!");
    }
}
