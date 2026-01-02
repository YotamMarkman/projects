package Polymorphism.Payments;

public class PayPalPayment implements PaymentMethod {

    @Override
    public void pay(double amount) {
        // TODO Auto-generated method stub
        System.out.println("Redirecting to PayPal...");
        System.out.println("Checking Email address...");
        System.out.println("Paid $" + amount + " via PayPal.");
    }
    
}
