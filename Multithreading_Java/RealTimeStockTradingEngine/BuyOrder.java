package RealTimeStockTradingEngine;

public class BuyOrder extends Order {

    public BuyOrder(String symbol, int quantity, double price, String traderName) {
        super(symbol, quantity, price, traderName);
        //TODO Auto-generated constructor stub

    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "BUY";
    }
    
}
