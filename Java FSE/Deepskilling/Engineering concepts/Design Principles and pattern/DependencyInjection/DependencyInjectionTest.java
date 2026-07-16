package DependencyInjection;

/**
 * Exercise 11: Implementing Dependency Injection
 * 
 * Scenario: A customer management application where the service class
 * depends on a repository class. Constructor injection is used.
 */

// --- Repository Interface ---
interface CustomerRepository {
    String findCustomerById(int id);
    void saveCustomer(int id, String name);
}

// --- Concrete Repository ---
class CustomerRepositoryImpl implements CustomerRepository {
    @Override
    public String findCustomerById(int id) {
        // Simulating database lookup
        System.out.println("  [Repository] Looking up customer with ID: " + id);
        if (id == 1) return "Arun Kumar";
        if (id == 2) return "Priya Sharma";
        if (id == 3) return "Rahul Verma";
        return null;
    }

    @Override
    public void saveCustomer(int id, String name) {
        System.out.println("  [Repository] Saving customer: ID=" + id + ", Name=" + name);
    }
}

// --- Service Class (depends on CustomerRepository) ---
class CustomerService {
    private final CustomerRepository repository;

    // Constructor Injection - dependency is injected via constructor
    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public String getCustomerName(int id) {
        String name = repository.findCustomerById(id);
        if (name != null) {
            System.out.println("  [Service] Found customer: " + name);
        } else {
            System.out.println("  [Service] Customer not found for ID: " + id);
        }
        return name;
    }

    public void addCustomer(int id, String name) {
        System.out.println("  [Service] Adding new customer...");
        repository.saveCustomer(id, name);
    }
}

// --- Test Class ---
public class DependencyInjectionTest {
    public static void main(String[] args) {
        System.out.println("=== Dependency Injection Demo ===\n");

        // Create the dependency (repository)
        CustomerRepository repository = new CustomerRepositoryImpl();

        // Inject dependency into service via constructor
        CustomerService service = new CustomerService(repository);

        // Use the service
        System.out.println("--- Finding customers ---");
        service.getCustomerName(1);
        service.getCustomerName(2);
        service.getCustomerName(99);

        System.out.println("\n--- Adding a new customer ---");
        service.addCustomer(4, "Sneha Patel");

        System.out.println("\n✓ Service depends on interface, not implementation!");
        System.out.println("  The repository can be easily swapped (e.g., for testing).");
    }
}
