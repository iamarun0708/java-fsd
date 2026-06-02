import java.util.HashMap;
import java.util.Scanner;

public class HashMapDemo {
    public static void main(String[] args) {
        HashMap<Integer, String> studentMap = new HashMap<>();
        Scanner scanner = new Scanner(System.in);

        studentMap.put(101, "Alice");
        studentMap.put(102, "Bob");
        studentMap.put(103, "Charlie");

        System.out.print("Enter student ID to retrieve name: ");
        int id = scanner.nextInt();

        if (studentMap.containsKey(id)) {
            System.out.println("Student Name: " + studentMap.get(id));
        } else {
            System.out.println("Student ID not found.");
        }
    }
}
