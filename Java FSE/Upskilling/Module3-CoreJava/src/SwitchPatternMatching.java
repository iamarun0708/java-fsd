public class SwitchPatternMatching {
    public static void printType(Object obj) {
        String typeInfo = switch (obj) {
            case Integer i -> "Integer: " + i;
            case String s -> "String: " + s;
            case Double d -> "Double: " + d;
            case null -> "null object";
            default -> "Unknown type: " + obj.toString();
        };
        System.out.println(typeInfo);
    }

    public static void main(String[] args) {
        printType(100);
        printType("Hello");
        printType(45.67);
        printType(null);
    }
}
