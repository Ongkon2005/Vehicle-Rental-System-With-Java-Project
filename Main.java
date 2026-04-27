import java.util.Scanner;

public class Main {

    public static void main(String[] args){

        Scanner sc = new Scanner(System.in);

        System.out.println("===== VEHICLE RENTAL SYSTEM =====");
        System.out.println("1. Registration");
        System.out.println("2. Login");

        System.out.print("Enter choice: ");
        int choice = sc.nextInt();
        sc.nextLine();

        User user;

        // REGISTRATION
        if(choice == 1){

            System.out.println("\n--- Registration ---");

            System.out.print("Enter Email or Phone: ");
            String email = sc.nextLine();

            System.out.print("Enter Password: ");
            String password = sc.nextLine();

            user = new User(email,password);

            System.out.println("\nRegistration Successful!");
        }

        // LOGIN
        else{

            System.out.println("\n--- Login ---");

            System.out.print("Enter Email or Phone: ");
            String email = sc.nextLine();

            System.out.print("Enter Password: ");
            String password = sc.nextLine();

            user = new User(email,password);

            System.out.println("\nLogin Successful!");
        }

        // VEHICLE TYPE MENU
        System.out.println("\n===== Select Vehicle Type =====");
        System.out.println("1. Car");
        System.out.println("2. Bike");
        System.out.println("3. Truck");

        System.out.print("Enter choice: ");
        int type = sc.nextInt();

        RentalService service = new RentalService();

        if(type == 1){
            service.addVehicle(new Car("Toyota",5000));
            service.addVehicle(new Car("Honda Civic",4500));
        }
        else if(type == 2){
            service.addVehicle(new Bike("Yamaha R15",1000));
            service.addVehicle(new Bike("Suzuki Gixxer",1200));
        }
        else if(type == 3){
            service.addVehicle(new Truck("Volvo Truck",8000));
            service.addVehicle(new Truck("Tata Truck",7000));
        }

        service.showVehicles();

        // SELECT VEHICLE
        System.out.print("\nSelect Vehicle: ");
        int vChoice = sc.nextInt();

        Vehicle selected = service.getVehicle(vChoice-1);

        System.out.println("\nPer Day Price: "
                + selected.getPricePerDay() + " Tk");

        // DAYS INPUT
        System.out.print("How many days you want: ");
        int days = sc.nextInt();

        Billing bill = new Billing();

        double total = bill.calculateBill(
                selected.getPricePerDay(),days);

        // BILL SHOW
        System.out.println("\n----- BILL -----");
        System.out.println("Total Amount: " + total + " Tk");

        // PAYMENT MENU
        System.out.println("\n===== PAYMENT METHOD =====");
        System.out.println("1. Cash");
        System.out.println("2. Mobile Banking (bKash/Nagad)");

        System.out.print("Select payment option: ");
        int payChoice = sc.nextInt();

        if(payChoice == 1){

            System.out.println("\nPayment Method: Cash");
            System.out.println("Please pay " + total + " Tk in cash.");
        }

        else if(payChoice == 2){

            System.out.println("\nPayment Method: Mobile Banking");
            System.out.println("Send " + total + " Tk to merchant number.");
            System.out.println("Payment Successful (Demo)");
        }

        else{
            System.out.println("Invalid Payment Option!");
        }

        System.out.println("\nVehicle Booked Successfully!");
        System.out.println("Thank you for using our system.");
    }
}
