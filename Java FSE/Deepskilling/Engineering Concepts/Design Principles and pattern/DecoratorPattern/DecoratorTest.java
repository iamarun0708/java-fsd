package DecoratorPattern;

/**
 * Exercise 5: Implementing the Decorator Pattern
 * 
 * Scenario: A notification system where notifications can be sent
 * via multiple channels (Email, SMS, Slack) dynamically.
 */

// --- Component Interface ---
interface Notifier {
    void send(String message);
}

// --- Concrete Component ---
class EmailNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("  [Email] Sending email: " + message);
    }
}

// --- Abstract Decorator ---
abstract class NotifierDecorator implements Notifier {
    protected Notifier wrappedNotifier;

    public NotifierDecorator(Notifier notifier) {
        this.wrappedNotifier = notifier;
    }

    @Override
    public void send(String message) {
        wrappedNotifier.send(message);
    }
}

// --- Concrete Decorators ---
class SMSNotifierDecorator extends NotifierDecorator {
    public SMSNotifierDecorator(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String message) {
        super.send(message);  // Delegate to wrapped notifier
        sendSMS(message);     // Add SMS functionality
    }

    private void sendSMS(String message) {
        System.out.println("  [SMS]   Sending SMS: " + message);
    }
}

class SlackNotifierDecorator extends NotifierDecorator {
    public SlackNotifierDecorator(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String message) {
        super.send(message);     // Delegate to wrapped notifier
        sendSlack(message);      // Add Slack functionality
    }

    private void sendSlack(String message) {
        System.out.println("  [Slack] Sending Slack: " + message);
    }
}

// --- Test Class ---
public class DecoratorTest {
    public static void main(String[] args) {
        System.out.println("=== Decorator Pattern Demo ===\n");

        // Email only
        System.out.println("1. Email only:");
        Notifier emailOnly = new EmailNotifier();
        emailOnly.send("Server is up!");

        // Email + SMS
        System.out.println("\n2. Email + SMS:");
        Notifier emailAndSMS = new SMSNotifierDecorator(new EmailNotifier());
        emailAndSMS.send("Deployment successful!");

        // Email + SMS + Slack (stacked decorators)
        System.out.println("\n3. Email + SMS + Slack:");
        Notifier allChannels = new SlackNotifierDecorator(
                                   new SMSNotifierDecorator(
                                       new EmailNotifier()));
        allChannels.send("CRITICAL: Database down!");

        System.out.println("\n✓ Decorators allow adding notification channels dynamically!");
    }
}
