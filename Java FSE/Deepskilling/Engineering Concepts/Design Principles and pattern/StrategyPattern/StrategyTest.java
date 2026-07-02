package StrategyPattern;

/**
 * Exercise 8: Implementing the Strategy Pattern
 * 
 * Scenario: A payment system where different payment methods
 * (Credit Card, PayPal) can be selected at runtime.
 */

// --- Strategy Interface ---
interface PaymentStrategy {
    void pay(double amount);
}

// --- Concrete Strategies ---
class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cardHolderName;

    public CreditCardPayment(String cardNumber, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
    }

    @Override
    public void pay(double amount) {
        String maskedCard = "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
        System.out.println("  Paid $" + amount + " via Credit Card (" + maskedCard + ", " + cardHolderName + ")");
    }
}

class PayPalPayment implements PaymentStrategy {
    private String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public void pay(double amount) {
        System.out.println("  Paid $" + amount + " via PayPal (account: " + email + ")");
    }
}

class CryptoPayment implements PaymentStrategy {
    private String walletAddress;

    public CryptoPayment(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    @Override
    public void pay(double amount) {
        System.out.println("  Paid $" + amount + " via Crypto (wallet: " + walletAddress + ")");
    }
}

// --- Context Class ---
class PaymentContext {
    private PaymentStrategy strategy;

    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void executePayment(double amount) {
        if (strategy == null) {
            System.out.println("  No payment method selected!");
            return;
        }
        strategy.pay(amount);
    }
}

// --- Test Class ---
public class StrategyTest {
    public static void main(String[] args) {
        System.out.println("=== Strategy Pattern Demo ===\n");

        PaymentContext context = new PaymentContext();

        // Pay with Credit Card
        System.out.println("Order 1 - Using Credit Card:");
        context.setPaymentStrategy(new CreditCardPayment("1234567890123456", "John Doe"));
        context.executePayment(250.00);

        // Switch strategy at runtime to PayPal
        System.out.println("\nOrder 2 - Using PayPal:");
        context.setPaymentStrategy(new PayPalPayment("john@example.com"));
        context.executePayment(89.99);

        // Switch strategy at runtime to Crypto
        System.out.println("\nOrder 3 - Using Cryptocurrency:");
        context.setPaymentStrategy(new CryptoPayment("0xABC...DEF"));
        context.executePayment(500.00);

        System.out.println("\n✓ Payment strategy can be switched at runtime!");
    }
}
