package Polymorphism.Payments;

public class PaymentProcessor{

    public void process(PaymentMethod paymentMethod, double amount) {
        paymentMethod.pay(amount);
    }
}
