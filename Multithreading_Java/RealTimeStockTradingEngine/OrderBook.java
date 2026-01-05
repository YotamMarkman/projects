package RealTimeStockTradingEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OrderBook {
    // BUG 1: These lists will crash if two threads add orders at the same time.
    private List<BuyOrder> buyOrders = new CopyOnWriteArrayList<>();
    private List<SellOrder> sellOrders = new CopyOnWriteArrayList<>();

    public synchronized void addOrder(Order order) {
        if (order instanceof BuyOrder) {
            buyOrders.add((BuyOrder) order);
        } else {
            sellOrders.add((SellOrder) order);
        }
        matchOrders();
    }

    public synchronized void matchOrders() {
        // BUG 2: This nested loop logic is dangerous
        for (BuyOrder buy : buyOrders) {
            for (SellOrder sell : sellOrders) {
                
                // BUG 3: LOGIC ERROR
                // He thinks: "If I want to buy cheap, I should match with a expensive seller?"
                if (buy.price >= sell.price) { 
                    System.out.println("TRADE EXECUTED: " + buy.quantity + " shares.");
                    
                    // BUG 4: CRASH ERROR
                    // You cannot remove items from a list while looping through it like this!
                    buyOrders.remove(buy);
                    sellOrders.remove(sell);
                    return; 
                }
            }
        }
    }
}