package RealTimeStockTradingEngine;

public abstract class Order {
    String symbol;
    int quantity;
    double price;
    String traderName;

    public Order(String symbol, int quantity, double price, String traderName) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.traderName = traderName;
    }

    public abstract String getType();
}
