import java.lang.reflect.Method;

public class ReflectionDemo {
    public static void main(String[] args) {
        try {
            Class<?> clazz = Class.forName("java.lang.String");
            
            System.out.println("Methods declared in String class:");
            Method[] methods = clazz.getDeclaredMethods();
            for (int i = 0; i < Math.min(methods.length, 5); i++) {
                System.out.println(" - " + methods[i].getName());
            }

            Object strInstance = clazz.getDeclaredConstructor(String.class).newInstance("Reflection Test");
            Method lengthMethod = clazz.getMethod("length");
            Object lengthVal = lengthMethod.invoke(strInstance);
            System.out.println("\nInvoked length(): " + lengthVal);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
