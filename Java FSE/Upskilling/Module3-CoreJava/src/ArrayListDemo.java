import java.util.ArrayList;
import java.util.Scanner;

public class ArrayListDemo {
    public static void main(String[] args) {
        ArrayList<String> students = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        String choice = "yes";

        while (choice.equalsIgnoreCase("yes")) {
            System.out.print("Enter student name: ");
            String name = scanner.nextLine();
            students.add(name);
            System.out.print("Do you want to add another name? (yes/no): ");
            choice = scanner.nextLine();
        }

        System.out.println("List of Student Names:");
        for (String s : students) {
            System.out.println(s);
        }
    }
}
