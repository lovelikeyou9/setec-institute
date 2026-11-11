import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Base payment class
class Payment {
    public void pay(double amount) {
        System.out.println("Payment of $" + amount);
    }
}

// Cash payment
class Cash extends Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Product paid by Cash: $" + amount);
    }
}

// Card payment
class Card extends Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Product paid by Card: $" + amount);
    }
}

// Mobile QR payment
class Mobile extends Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Product paid by Mobile (QRCode Scan): $" + amount);
    }
}

// Main program
public class test_2_Polymorphism {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Payment> payments = new ArrayList<>();
        double totalCash = 0, totalCard = 0, totalMobile = 0;

        boolean moreCustomers = true;
        int customerCount = 1;

        while (moreCustomers) {
            System.out.println("Customer " + customerCount);
            System.out.print("Price = ");
            double price = sc.nextDouble();
            System.out.print("Qty = ");
            int qty = sc.nextInt();
            double amount = price * qty;
            System.out.println("Amount = $" + amount);

            System.out.println("\nPayment options:");
            System.out.println("1. Cash\n2. Card\n3. Mobile (QRCode Scan)");
            System.out.print("Please choose: ");
            int choice = sc.nextInt();

            Payment payment = null;
            switch (choice) {
                case 1 -> payment = new Cash();
                case 2 -> payment = new Card();
                case 3 -> payment = new Mobile();
                default -> System.out.println("Invalid choice!");
            }

            if (payment != null) {
                payment.pay(amount);

                // Track total income by type
                if (payment instanceof Cash) totalCash += amount;
                if (payment instanceof Card) totalCard += amount;
                if (payment instanceof Mobile) totalMobile += amount;

                payments.add(payment);
            }

            System.out.print("One more customer? (y/n): ");
            String ans = sc.next();
            moreCustomers = ans.equalsIgnoreCase("y");
            customerCount++;
        }

        // Show totals
        System.out.println("\nTotal income:");
        System.out.println("Income by Cash: $" + totalCash);
        System.out.println("Income by Card: $" + totalCard);
        System.out.println("Income by Mobile Scan: $" + totalMobile);
    }
}
