public class OperatorPrecedence {
    public static void main(String[] args) {
        int result = 10 + 5 * 2;
        System.out.println("Expression: 10 + 5 * 2");
        System.out.println("Result: " + result);
        System.out.println("Explanation: Multiplication (*) has higher precedence than addition (+), so 5 * 2 is evaluated first to 10, then 10 is added to 10, resulting in 20.");
    }
}
