package Polymorphism.Observer;

public class EmailService implements OrderObserver{

    @Override
    public void onOrderPlaced(String orderId) {
        // TODO Auto-generated method stub
        System.out.println("Sending Email to OderId " + orderId);
    }
    
}
