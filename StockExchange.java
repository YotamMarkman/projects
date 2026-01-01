import java.util.ArrayList;
import java.util.List;

public class StockExchange {
    private List<Double> buyOrders;
    private List<Double> sellOrders;

    public StockExchange() {
        this.buyOrders = new ArrayList<>();
        this.sellOrders = new ArrayList<>();
    }

    public synchronized void placeOrder(String type, double price) {
        if (type.equals("BUY")) {
            for (int i = 0; i < sellOrders.size(); i++) {
                double sellerPrice = sellOrders.get(i);
                if (sellerPrice <= price) {
                    System.out.println("MATCHED: Buying @ " + price + " from Seller @ " + sellerPrice);
                    sellOrders.remove(i); 
                    return; 
                }
            }
            buyOrders.add(price);
            System.out.println("Order Added: BUY @ " + price);

        } else if (type.equals("SELL")) {
            for (int i = 0; i < buyOrders.size(); i++) {
                double buyerPrice = buyOrders.get(i);
                if (buyerPrice >= price) {
                    System.out.println("MATCHED: Selling @ " + price + " to Buyer @ " + buyerPrice);
                    buyOrders.remove(i);
                    return; 
                }
            }
            sellOrders.add(price);
            System.out.println("Order Added: SELL @ " + price);
        }
    }
}