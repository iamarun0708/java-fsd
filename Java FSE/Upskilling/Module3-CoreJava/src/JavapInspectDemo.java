public class JavapInspectDemo {
    public int calculateSum(int a, int b) {
        int sum = a + b;
        return sum;
    }

    public static void main(String[] args) {
        JavapInspectDemo demo = new JavapInspectDemo();
        System.out.println("Result: " + demo.calculateSum(5, 7));
    }
}
