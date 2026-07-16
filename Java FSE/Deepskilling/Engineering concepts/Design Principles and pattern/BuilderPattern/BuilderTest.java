package BuilderPattern;

/**
 * Exercise 3: Implementing the Builder Pattern
 * 
 * Scenario: A system to create complex Computer objects
 * with multiple optional parts.
 */

// --- Product Class ---
class Computer {
    // Required parameters
    private String CPU;
    private String RAM;

    // Optional parameters
    private String storage;
    private String graphicsCard;
    private String operatingSystem;
    private boolean bluetoothEnabled;
    private boolean wifiEnabled;

    // Private constructor - only accessible via Builder
    private Computer(Builder builder) {
        this.CPU = builder.CPU;
        this.RAM = builder.RAM;
        this.storage = builder.storage;
        this.graphicsCard = builder.graphicsCard;
        this.operatingSystem = builder.operatingSystem;
        this.bluetoothEnabled = builder.bluetoothEnabled;
        this.wifiEnabled = builder.wifiEnabled;
    }

    @Override
    public String toString() {
        return "Computer Configuration:\n" +
               "  CPU            : " + CPU + "\n" +
               "  RAM            : " + RAM + "\n" +
               "  Storage        : " + storage + "\n" +
               "  Graphics Card  : " + graphicsCard + "\n" +
               "  OS             : " + operatingSystem + "\n" +
               "  Bluetooth      : " + bluetoothEnabled + "\n" +
               "  WiFi           : " + wifiEnabled;
    }

    // --- Static Nested Builder Class ---
    public static class Builder {
        // Required parameters
        private String CPU;
        private String RAM;

        // Optional parameters - initialized to defaults
        private String storage = "256GB SSD";
        private String graphicsCard = "Integrated";
        private String operatingSystem = "None";
        private boolean bluetoothEnabled = false;
        private boolean wifiEnabled = true;

        public Builder(String CPU, String RAM) {
            this.CPU = CPU;
            this.RAM = RAM;
        }

        public Builder setStorage(String storage) {
            this.storage = storage;
            return this;
        }

        public Builder setGraphicsCard(String graphicsCard) {
            this.graphicsCard = graphicsCard;
            return this;
        }

        public Builder setOperatingSystem(String operatingSystem) {
            this.operatingSystem = operatingSystem;
            return this;
        }

        public Builder setBluetoothEnabled(boolean bluetoothEnabled) {
            this.bluetoothEnabled = bluetoothEnabled;
            return this;
        }

        public Builder setWifiEnabled(boolean wifiEnabled) {
            this.wifiEnabled = wifiEnabled;
            return this;
        }

        public Computer build() {
            return new Computer(this);
        }
    }
}

// --- Test Class ---
public class BuilderTest {
    public static void main(String[] args) {
        System.out.println("=== Builder Pattern Demo ===\n");

        // Gaming PC - fully loaded
        Computer gamingPC = new Computer.Builder("Intel i9-13900K", "32GB DDR5")
                .setStorage("2TB NVMe SSD")
                .setGraphicsCard("NVIDIA RTX 4090")
                .setOperatingSystem("Windows 11")
                .setBluetoothEnabled(true)
                .setWifiEnabled(true)
                .build();

        System.out.println("--- Gaming PC ---");
        System.out.println(gamingPC);

        // Office PC - basic config
        Computer officePC = new Computer.Builder("Intel i5-13400", "16GB DDR4")
                .setStorage("512GB SSD")
                .setOperatingSystem("Windows 11")
                .build();

        System.out.println("\n--- Office PC ---");
        System.out.println(officePC);

        // Server - minimal
        Computer server = new Computer.Builder("AMD EPYC 9654", "128GB ECC")
                .setStorage("4TB NVMe RAID")
                .setOperatingSystem("Ubuntu Server 22.04")
                .setWifiEnabled(false)
                .build();

        System.out.println("\n--- Server ---");
        System.out.println(server);
    }
}
