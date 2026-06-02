import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileWriteDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a string to write to file: ");
        String text = scanner.nextLine();

        try (FileWriter writer = new FileWriter("output.txt")) {
            writer.write(text);
            System.out.println("Data successfully written to output.txt");
        } catch (IOException e) {
            System.out.println("An error occurred during file writing: " + e.getMessage());
        }
    }
}
