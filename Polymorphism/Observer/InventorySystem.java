package Polymorphism.Observer;

public class InventorySystem implements OrderObserver {

    @Override
    public void onOrderPlaced(String orderId) {
        // TODO Auto-generated method stub
        System.out.println("Deduction stock for order " + orderId);
    }
    
}
