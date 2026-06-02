public class Car {
    private String make;
    private String model;
    private int year;

    public Car(String make, String model, int year) {
        this.make = make;
        this.model = model;
        this.year = year;
    }

    public void displayDetails() {
        System.out.println("Car Make: " + make + ", Model: " + model + ", Year: " + year);
    }

    public static void main(String[] args) {
        Car car = new Car("Toyota", "Corolla", 2022);
        car.displayDetails();
    }
}
