import java.util.Scanner;
import java.util.Random;

public class NumberGuessingGame {
    public static void main(String[] args) {
        Random random = new Random();
        int target = random.nextInt(100) + 1;
        Scanner scanner = new Scanner(System.in);
        int guess = 0;
        System.out.println("Guess the number between 1 and 100!");
        while (guess != target) {
            System.out.print("Enter your guess: ");
            guess = scanner.nextInt();
            if (guess > target) {
                System.out.println("Too high!");
            } else if (guess < target) {
                System.out.println("Too low!");
            }
        }
        System.out.println("Correct! You guessed the number.");
    }
}
