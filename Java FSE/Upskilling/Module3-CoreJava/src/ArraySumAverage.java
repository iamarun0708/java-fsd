import java.util.Scanner;

public class ArraySumAverage {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of elements: ");
        int n = scanner.nextInt();
        double[] arr = new double[n];
        double sum = 0;
        for (int i = 0; i < n; i++) {
            System.out.print("Enter element " + (i + 1) + ": ");
            arr[i] = scanner.nextDouble();
            sum += arr[i];
        }
        double average = n > 0 ? sum / n : 0;
        System.out.println("Sum: " + sum);
        System.out.println("Average: " + average);
    }
}
