package Polymorphism.Payments;

public class CreditCardPayment implements PaymentMethod{

    @Override
    public void pay(double amount) {
        // TODO Auto-generated method stub
        System.out.println("Connecting to Bank...");
        System.out.println("Verifying Card details...");
        System.out.println("Charged $" + amount + " to Credit Card.");
    }
    
}
