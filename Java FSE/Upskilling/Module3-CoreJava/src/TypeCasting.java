public class TypeCasting {
    public static void main(String[] args) {
        double originalDouble = 9.78;
        int castedInt = (int) originalDouble;
        System.out.println("double value: " + originalDouble);
        System.out.println("casted to int: " + castedInt);

        int originalInt = 15;
        double castedDouble = (double) originalInt;
        System.out.println("int value: " + originalInt);
        System.out.println("casted to double: " + castedDouble);
    }
}
