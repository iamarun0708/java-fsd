import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

record Person(String name, int age) {}

public class RecordsDemo {
    public static void main(String[] args) {
        Person p1 = new Person("Alice", 25);
        Person p2 = new Person("Bob", 17);
        Person p3 = new Person("Charlie", 30);

        List<Person> people = Arrays.asList(p1, p2, p3);

        System.out.println("All People:");
        people.forEach(System.out.println);

        List<Person> adults = people.stream()
                                    .filter(p -> p.age() >= 18)
                                    .collect(Collectors.toList());

        System.out.println("\nAdults (Age >= 18):");
        adults.forEach(System.out::println);
    }
}
