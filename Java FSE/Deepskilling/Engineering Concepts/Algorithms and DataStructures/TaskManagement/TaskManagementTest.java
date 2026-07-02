package TaskManagement;

/**
 * Exercise 5: Task Management System
 * 
 * Implements a Singly Linked List to manage tasks.
 * Supports add, search, traverse, and delete operations.
 */

// --- Task Class ---
class Task {
    private int taskId;
    private String taskName;
    private String status;

    public Task(int taskId, String taskName, String status) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.status = status;
    }

    public int getTaskId() { return taskId; }
    public String getTaskName() { return taskName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("ID:%-4d | %-20s | %s", taskId, taskName, status);
    }
}

// --- Node for Linked List ---
class Node {
    Task task;
    Node next;

    public Node(Task task) {
        this.task = task;
        this.next = null;
    }
}

// --- Singly Linked List ---
class TaskLinkedList {
    private Node head;
    private int size;

    public TaskLinkedList() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Add at end - O(n) for singly linked list
     * Could be O(1) with tail pointer
     */
    public void addTask(Task task) {
        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
        System.out.println("  Added: " + task.getTaskName());
    }

    /**
     * Add at beginning - O(1)
     */
    public void addTaskAtBeginning(Task task) {
        Node newNode = new Node(task);
        newNode.next = head;
        head = newNode;
        size++;
        System.out.println("  Added at beginning: " + task.getTaskName());
    }

    /**
     * Search by ID - O(n)
     */
    public Task searchTask(int taskId) {
        Node current = head;
        while (current != null) {
            if (current.task.getTaskId() == taskId) {
                return current.task;
            }
            current = current.next;
        }
        return null;
    }

    /**
     * Traverse all - O(n)
     */
    public void traverse() {
        System.out.println("  --- Task List (size: " + size + ") ---");
        Node current = head;
        int index = 0;
        while (current != null) {
            System.out.println("  [" + index + "] " + current.task);
            current = current.next;
            index++;
        }
    }

    /**
     * Delete by ID - O(n)
     */
    public boolean deleteTask(int taskId) {
        if (head == null) return false;

        // If head node is the one to delete
        if (head.task.getTaskId() == taskId) {
            String name = head.task.getTaskName();
            head = head.next;
            size--;
            System.out.println("  Deleted: " + name);
            return true;
        }

        // Search for the node to delete
        Node current = head;
        while (current.next != null) {
            if (current.next.task.getTaskId() == taskId) {
                String name = current.next.task.getTaskName();
                current.next = current.next.next;  // Skip the node
                size--;
                System.out.println("  Deleted: " + name);
                return true;
            }
            current = current.next;
        }

        System.out.println("  Task not found: ID " + taskId);
        return false;
    }
}

// --- Test Class ---
public class TaskManagementTest {
    public static void main(String[] args) {
        System.out.println("=== Exercise 5: Task Management System (Linked List) ===\n");

        /*
         * Linked List Types:
         * 
         * 1. Singly Linked List: Each node points to next
         *    [Data|Next] -> [Data|Next] -> [Data|null]
         * 
         * 2. Doubly Linked List: Each node points to both prev and next
         *    null<-[Prev|Data|Next] <-> [Prev|Data|Next] <-> [Prev|Data|null]
         * 
         * Time Complexity (Singly Linked List):
         *   Add at beginning : O(1)
         *   Add at end       : O(n)  (O(1) with tail pointer)
         *   Search           : O(n)
         *   Traverse         : O(n)
         *   Delete           : O(n)
         * 
         * Advantages over Arrays:
         * - Dynamic size (no fixed capacity)
         * - Efficient insertion/deletion at beginning (O(1))
         * - No wasted memory from unused slots
         * - No shifting needed for insert/delete
         */

        TaskLinkedList taskList = new TaskLinkedList();

        // Add tasks
        System.out.println("--- Adding Tasks ---");
        taskList.addTask(new Task(1, "Design Database", "In Progress"));
        taskList.addTask(new Task(2, "Build REST API", "Pending"));
        taskList.addTask(new Task(3, "Write Unit Tests", "Pending"));
        taskList.addTask(new Task(4, "Deploy to Cloud", "Pending"));

        // Add at beginning
        taskList.addTaskAtBeginning(new Task(0, "Setup Project", "Completed"));

        // Traverse
        System.out.println("\n--- All Tasks ---");
        taskList.traverse();

        // Search
        System.out.println("\n--- Searching for Task ID 2 ---");
        Task found = taskList.searchTask(2);
        System.out.println("  Found: " + (found != null ? found : "Not found"));

        // Delete
        System.out.println("\n--- Deleting Task ID 3 ---");
        taskList.deleteTask(3);

        System.out.println("\n--- After Deletion ---");
        taskList.traverse();

        System.out.println("\n✓ Linked Lists are ideal for dynamic data with frequent insertions/deletions!");
    }
}
