public class Car extends Vehicle {
    public Car(String name, double price) {
        super(name, price, "Car");
    }
    @Override
    void showDetails() {
        System.out.println("Car: " + vehicleName + " | Price: " + pricePerDay + " Tk/day");
    }
}
