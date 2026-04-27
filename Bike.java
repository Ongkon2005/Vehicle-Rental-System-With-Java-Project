public class Bike extends Vehicle {
    public Bike(String name, double price) {
        super(name, price, "Bike");
    }
    @Override
    void showDetails() {
        System.out.println("Bike: " + vehicleName + " | Price: " + pricePerDay + " Tk/day");
    }
}
