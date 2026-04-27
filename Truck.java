public class Truck extends Vehicle {
    public Truck(String name, double price) {
        super(name, price, "Truck");
    }
    @Override
    void showDetails() {
        System.out.println("Truck: " + vehicleName + " | Price: " + pricePerDay + " Tk/day");
    }
}
