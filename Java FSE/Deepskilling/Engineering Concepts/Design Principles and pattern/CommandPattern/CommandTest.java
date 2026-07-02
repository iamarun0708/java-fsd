package CommandPattern;

/**
 * Exercise 9: Implementing the Command Pattern
 * 
 * Scenario: A home automation system where commands can be issued
 * to turn devices on or off.
 */

// --- Command Interface ---
interface Command {
    void execute();
    void undo();
}

// --- Receiver Class ---
class Light {
    private String location;

    public Light(String location) {
        this.location = location;
    }

    public void turnOn() {
        System.out.println("  " + location + " light is ON");
    }

    public void turnOff() {
        System.out.println("  " + location + " light is OFF");
    }
}

class Fan {
    private String location;

    public Fan(String location) {
        this.location = location;
    }

    public void turnOn() {
        System.out.println("  " + location + " fan is ON");
    }

    public void turnOff() {
        System.out.println("  " + location + " fan is OFF");
    }
}

// --- Concrete Commands ---
class LightOnCommand implements Command {
    private Light light;

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.turnOn();
    }

    @Override
    public void undo() {
        light.turnOff();
    }
}

class LightOffCommand implements Command {
    private Light light;

    public LightOffCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.turnOff();
    }

    @Override
    public void undo() {
        light.turnOn();
    }
}

class FanOnCommand implements Command {
    private Fan fan;

    public FanOnCommand(Fan fan) {
        this.fan = fan;
    }

    @Override
    public void execute() {
        fan.turnOn();
    }

    @Override
    public void undo() {
        fan.turnOff();
    }
}

class FanOffCommand implements Command {
    private Fan fan;

    public FanOffCommand(Fan fan) {
        this.fan = fan;
    }

    @Override
    public void execute() {
        fan.turnOff();
    }

    @Override
    public void undo() {
        fan.turnOn();
    }
}

// --- Invoker Class ---
class RemoteControl {
    private Command command;
    private Command lastCommand;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton() {
        if (command != null) {
            command.execute();
            lastCommand = command;
        }
    }

    public void pressUndo() {
        if (lastCommand != null) {
            System.out.println("  [Undo]");
            lastCommand.undo();
        }
    }
}

// --- Test Class ---
public class CommandTest {
    public static void main(String[] args) {
        System.out.println("=== Command Pattern Demo ===\n");

        // Create receivers
        Light livingRoomLight = new Light("Living Room");
        Light bedroomLight = new Light("Bedroom");
        Fan ceilingFan = new Fan("Ceiling");

        // Create commands
        Command livingLightOn = new LightOnCommand(livingRoomLight);
        Command livingLightOff = new LightOffCommand(livingRoomLight);
        Command bedroomLightOn = new LightOnCommand(bedroomLight);
        Command fanOn = new FanOnCommand(ceilingFan);
        Command fanOff = new FanOffCommand(ceilingFan);

        // Create invoker
        RemoteControl remote = new RemoteControl();

        // Execute commands
        System.out.println("--- Pressing buttons on remote ---");

        remote.setCommand(livingLightOn);
        remote.pressButton();

        remote.setCommand(bedroomLightOn);
        remote.pressButton();

        remote.setCommand(fanOn);
        remote.pressButton();

        // Undo last command
        System.out.println("\n--- Undoing last command ---");
        remote.pressUndo();

        // Turn off lights
        System.out.println("\n--- Turning everything off ---");
        remote.setCommand(livingLightOff);
        remote.pressButton();

        remote.setCommand(fanOff);
        remote.pressButton();

        System.out.println("\n✓ Commands decouple invoker from receiver!");
    }
}
