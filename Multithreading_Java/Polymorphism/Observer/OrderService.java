package Polymorphism.Observer;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    // Problem: OrderService creates and owns all these specific classes!
    private List<OrderObserver> list;
    
    public OrderService(){
        this.list = new ArrayList<>();
    }

    public void placeOrder(String orderId) {
        System.out.println("Order " + orderId + " placed successfully.");
        for (OrderObserver obs : list) {
            obs.onOrderPlaced(orderId);
        }  
    }

    public void subscribe(OrderObserver observer){
    // Add people to that list
        list.add(observer);    
    }


}
