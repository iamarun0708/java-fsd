package ObserverPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 7: Implementing the Observer Pattern
 * 
 * Scenario: A stock market monitoring application where multiple clients
 * are notified when stock prices change.
 */

// --- Observer Interface ---
interface Observer {
    void update(String stockName, double price);
}

// --- Subject Interface ---
interface Stock {
    void registerObserver(Observer observer);
    void deregisterObserver(Observer observer);
    void notifyObservers();
}

// --- Concrete Subject ---
class StockMarket implements Stock {
    private List<Observer> observers = new ArrayList<>();
    private String stockName;
    private double price;

    public StockMarket(String stockName, double initialPrice) {
        this.stockName = stockName;
        this.price = initialPrice;
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
        System.out.println("  Observer registered for " + stockName);
    }

    @Override
    public void deregisterObserver(Observer observer) {
        observers.remove(observer);
        System.out.println("  Observer deregistered from " + stockName);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(stockName, price);
        }
    }

    public void setPrice(double newPrice) {
        System.out.println("\n  >> " + stockName + " price changed: $" + price + " -> $" + newPrice);
        this.price = newPrice;
        notifyObservers();
    }
}

// --- Concrete Observers ---
class MobileApp implements Observer {
    private String appName;

    public MobileApp(String appName) {
        this.appName = appName;
    }

    @Override
    public void update(String stockName, double price) {
        System.out.println("     [MobileApp:" + appName + "] Notification - " + stockName + " is now $" + price);
    }
}

class WebApp implements Observer {
    private String webAppName;

    public WebApp(String webAppName) {
        this.webAppName = webAppName;
    }

    @Override
    public void update(String stockName, double price) {
        System.out.println("     [WebApp:" + webAppName + "] Dashboard updated - " + stockName + " = $" + price);
    }
}

// --- Test Class ---
public class ObserverTest {
    public static void main(String[] args) {
        System.out.println("=== Observer Pattern Demo ===\n");

        // Create stock
        StockMarket appleStock = new StockMarket("AAPL", 150.00);

        // Create observers
        Observer mobileApp = new MobileApp("StockTracker");
        Observer webApp = new WebApp("TradingDashboard");
        Observer alertApp = new MobileApp("PriceAlerts");

        // Register observers
        appleStock.registerObserver(mobileApp);
        appleStock.registerObserver(webApp);
        appleStock.registerObserver(alertApp);

        // Price changes - all observers notified
        appleStock.setPrice(155.50);
        appleStock.setPrice(148.75);

        // Deregister one observer
        System.out.println();
        appleStock.deregisterObserver(alertApp);

        // Only remaining observers get notified
        appleStock.setPrice(160.00);

        System.out.println("\n✓ Observers are automatically notified of price changes!");
    }
}
