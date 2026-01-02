package RealTimeStockTradingEngine;

public class SellOrder extends Order {
    
    
    public SellOrder(String symbol, int quantity, double price, String traderName) {
        super(symbol, quantity, price, traderName);
        //TODO Auto-generated constructor stub
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "SELL";
    }
}
