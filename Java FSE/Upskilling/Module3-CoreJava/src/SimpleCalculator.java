import java.util.Scanner;

public class SimpleCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter first number: ");
        double num1 = scanner.nextDouble();
        System.out.print("Enter second number: ");
        double num2 = scanner.nextDouble();
        System.out.print("Choose an operation (+, -, *, /): ");
        char operation = scanner.next().charAt(0);
        double result = 0;
        switch (operation) {
            case '+': result = num1 + num2; break;
            case '-': result = num1 - num2; break;
            case '*': result = num1 * num2; break;
            case '/':
                if (num2 == 0) {
                    System.out.println("Error: Division by zero.");
                    return;
                }
                result = num1 / num2;
                break;
            default:
                System.out.println("Invalid operation.");
                return;
        }
        System.out.println("Result: " + result);
    }
}
