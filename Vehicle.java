public abstract class Vehicle {

    protected String vehicleName;
    protected double pricePerDay;
    protected String type;

    public Vehicle(String name, double price, String type) {
        this.vehicleName = name;
        this.pricePerDay = price;
        this.type = type;
    }

    public String getVehicleName() { return vehicleName; }
    public double getPricePerDay() { return pricePerDay; }
    public String getType() { return type; }

    abstract void showDetails();
}
