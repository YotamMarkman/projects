public class Main {
    public static void main(String[] args) {
        StockExchange exchange = new StockExchange();

        // 1. Thread A: The Seller
        // Wants to sell for $100. If no one is buying, they wait.
        Thread seller = new Thread(() -> {
            System.out.println("Seller: I want to sell AAPL for $100");
            exchange.placeOrder("SELL", 100.0);
        });

        // 2. Thread B: The Buyer
        // Wants to buy for $110. Since $110 >= $100, this should MATCH instantly.
        Thread buyer = new Thread(() -> {
            try { Thread.sleep(1000); } catch (Exception e) {} // Wait for Seller to arrive
            System.out.println("Buyer: I am willing to pay $110 for AAPL");
            exchange.placeOrder("BUY", 110.0);
        });

        seller.start();
        buyer.start();
    }
}