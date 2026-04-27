import java.util.ArrayList;

public class RentalService {

    ArrayList<Vehicle> vehicles = new ArrayList<>();

    public void addVehicle(Vehicle v) {
        vehicles.add(v);
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public Vehicle getVehicle(int index) {
        return vehicles.get(index);
    }

    public void showVehicles() {
        System.out.println("\nAvailable Vehicles:");
        for (int i = 0; i < vehicles.size(); i++) {
            System.out.print((i + 1) + ". ");
            vehicles.get(i).showDetails();
        }
    }
}
