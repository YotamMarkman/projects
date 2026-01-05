package Polymorphism.Payments;

public class CryptoPayment implements PaymentMethod{

    @Override
    public void pay(double amount) {
        // TODO Auto-generated method stub
        System.out.println("Connecting to Blockchain...");
        System.out.println("Verifying Wallet Address...");
        System.out.println("Transferred $" + amount + " in Crypto.");
    }
    
}
