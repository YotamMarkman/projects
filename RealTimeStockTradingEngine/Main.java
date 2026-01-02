package RealTimeStockTradingEngine;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Real-Time Stock Trading Engine Demo ===\n");
        
        OrderBook orderBook = new OrderBook();
        
        // Example 1: Simple Buy and Sell Match
        System.out.println("--- Example 1: Simple Match ---");
        System.out.println("Alice wants to BUY 100 shares of AAPL at $150.00");
        BuyOrder buy1 = new BuyOrder("AAPL", 100, 150.00, "Alice");
        orderBook.addOrder(buy1);
        
        System.out.println("Bob wants to SELL 100 shares of AAPL at $149.00");
        SellOrder sell1 = new SellOrder("AAPL", 100, 149.00, "Bob");
        orderBook.addOrder(sell1);
        System.out.println("→ Trade should execute (Buy $150 >= Sell $149)\n");
        
        Thread.sleep(500);
        
        // Example 2: No Match - Buyer price too low
        System.out.println("--- Example 2: No Match (Price Gap) ---");
        System.out.println("Charlie wants to BUY 50 shares of TSLA at $200.00");
        BuyOrder buy2 = new BuyOrder("TSLA", 50, 200.00, "Charlie");
        orderBook.addOrder(buy2);
        
        System.out.println("Diana wants to SELL 50 shares of TSLA at $210.00");
        SellOrder sell2 = new SellOrder("TSLA", 50, 210.00, "Diana");
        orderBook.addOrder(sell2);
        System.out.println("→ No trade (Buy $200 < Sell $210)\n");
        
        Thread.sleep(500);
        
        // Example 3: Multiple orders, first come first serve
        System.out.println("--- Example 3: Multiple Orders ---");
        System.out.println("Eve wants to BUY 200 shares of GOOGL at $2800.00");
        BuyOrder buy3 = new BuyOrder("GOOGL", 200, 2800.00, "Eve");
        orderBook.addOrder(buy3);
        
        System.out.println("Frank wants to BUY 150 shares of GOOGL at $2750.00");
        BuyOrder buy4 = new BuyOrder("GOOGL", 150, 2750.00, "Frank");
        orderBook.addOrder(buy4);
        
        System.out.println("Grace wants to SELL 100 shares of GOOGL at $2750.00");
        SellOrder sell3 = new SellOrder("GOOGL", 100, 2750.00, "Grace");
        orderBook.addOrder(sell3);
        System.out.println("→ Trade with Eve (first buyer at $2800 >= $2750)\n");
        
        Thread.sleep(500);
        
        // Example 4: Exact price match
        System.out.println("--- Example 4: Exact Price Match ---");
        System.out.println("Henry wants to BUY 75 shares of MSFT at $300.00");
        BuyOrder buy5 = new BuyOrder("MSFT", 75, 300.00, "Henry");
        orderBook.addOrder(buy5);
        
        System.out.println("Iris wants to SELL 75 shares of MSFT at $300.00");
        SellOrder sell4 = new SellOrder("MSFT", 75, 300.00, "Iris");
        orderBook.addOrder(sell4);
        System.out.println("→ Perfect match at $300.00\n");
        
        Thread.sleep(500);
        
        // Example 5: Large quantity trade
        System.out.println("--- Example 5: Large Volume Trade ---");
        System.out.println("Jack wants to BUY 10000 shares of NVDA at $500.00");
        BuyOrder buy6 = new BuyOrder("NVDA", 10000, 500.00, "Jack");
        orderBook.addOrder(buy6);
        
        System.out.println("Kate wants to SELL 10000 shares of NVDA at $495.00");
        SellOrder sell5 = new SellOrder("NVDA", 10000, 495.00, "Kate");
        orderBook.addOrder(sell5);
        System.out.println("→ Large trade executed!\n");
        
        System.out.println("=== Demo Complete ===");
        System.out.println("\nNote: This is a simplified order matching engine.");
        System.out.println("In production systems, partial fills, order types (limit/market),");
        System.out.println("and more sophisticated matching algorithms would be implemented.");
    }
}
