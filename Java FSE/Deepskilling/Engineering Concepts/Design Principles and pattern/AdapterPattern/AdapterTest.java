package AdapterPattern;

/**
 * Exercise 4: Implementing the Adapter Pattern
 * 
 * Scenario: A payment processing system integrating with multiple
 * third-party payment gateways with different interfaces.
 */

// --- Target Interface ---
interface PaymentProcessor {
    void processPayment(double amount);
    String getGatewayName();
}

// --- Adaptee Classes (third-party gateways with different APIs) ---
class StripeGateway {
    public void makePayment(double amount, String currency) {
        System.out.println("  [Stripe] Processing $" + amount + " " + currency + " payment via Stripe API");
    }
}

class PayPalGateway {
    public void sendPayment(double amount) {
        System.out.println("  [PayPal] Sending $" + amount + " via PayPal SDK");
    }
}

class SquareGateway {
    public void chargeAmount(String amountInCents) {
        System.out.println("  [Square] Charging " + amountInCents + " cents via Square API");
    }
}

// --- Adapter Classes ---
class StripeAdapter implements PaymentProcessor {
    private StripeGateway stripeGateway;

    public StripeAdapter(StripeGateway stripeGateway) {
        this.stripeGateway = stripeGateway;
    }

    @Override
    public void processPayment(double amount) {
        // Adapt to Stripe's interface (requires currency)
        stripeGateway.makePayment(amount, "USD");
    }

    @Override
    public String getGatewayName() {
        return "Stripe";
    }
}

class PayPalAdapter implements PaymentProcessor {
    private PayPalGateway payPalGateway;

    public PayPalAdapter(PayPalGateway payPalGateway) {
        this.payPalGateway = payPalGateway;
    }

    @Override
    public void processPayment(double amount) {
        payPalGateway.sendPayment(amount);
    }

    @Override
    public String getGatewayName() {
        return "PayPal";
    }
}

class SquareAdapter implements PaymentProcessor {
    private SquareGateway squareGateway;

    public SquareAdapter(SquareGateway squareGateway) {
        this.squareGateway = squareGateway;
    }

    @Override
    public void processPayment(double amount) {
        // Adapt: Square expects amount in cents as a String
        String amountInCents = String.valueOf((int) (amount * 100));
        squareGateway.chargeAmount(amountInCents);
    }

    @Override
    public String getGatewayName() {
        return "Square";
    }
}

// --- Test Class ---
public class AdapterTest {
    public static void main(String[] args) {
        System.out.println("=== Adapter Pattern Demo ===\n");

        // Create adapters for each payment gateway
        PaymentProcessor stripe = new StripeAdapter(new StripeGateway());
        PaymentProcessor paypal = new PayPalAdapter(new PayPalGateway());
        PaymentProcessor square = new SquareAdapter(new SquareGateway());

        // Process payments through a uniform interface
        PaymentProcessor[] processors = {stripe, paypal, square};
        double amount = 99.99;

        for (PaymentProcessor processor : processors) {
            System.out.println("Processing via " + processor.getGatewayName() + ":");
            processor.processPayment(amount);
            System.out.println();
        }

        System.out.println("✓ All gateways used through the same PaymentProcessor interface!");
    }
}
