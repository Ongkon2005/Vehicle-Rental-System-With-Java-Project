public class Billing {

    public double calculateBill(double pricePerDay, int days) {
        return pricePerDay * days;
    }

    public double calculateBill(double pricePerDay, int days, double discount) {
        return (pricePerDay * days) - discount;
    }

    public String processPayment(double amount, String method, String accountNumber) {
        if (method.equalsIgnoreCase("Cash")) {
            return "CASH_PAYMENT|Please pay " + String.format("%.2f", amount) + " Tk in cash at the counter.";
        } else if (method.equalsIgnoreCase("bKash")) {
            return "MOBILE_PAYMENT|bKash payment of " + String.format("%.2f", amount) +
                   " Tk sent to " + accountNumber + ". Transaction verified!";
        } else if (method.equalsIgnoreCase("Nagad")) {
            return "MOBILE_PAYMENT|Nagad payment of " + String.format("%.2f", amount) +
                   " Tk sent to " + accountNumber + ". Transaction verified!";
        } else if (method.equalsIgnoreCase("Card")) {
            return "CARD_PAYMENT|Card payment of " + String.format("%.2f", amount) +
                   " Tk processed. Authorization code: #" + (int)(Math.random() * 900000 + 100000);
        } else {
            return "ERROR|Invalid payment method!";
        }
    }
}
