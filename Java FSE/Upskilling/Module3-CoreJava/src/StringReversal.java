import java.util.Scanner;

public class StringReversal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a string: ");
        String str = scanner.nextLine();
        StringBuilder sb = new StringBuilder(str);
        System.out.println("Reversed string: " + sb.reverse().toString());
    }
}
