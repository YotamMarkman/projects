package RealTimeStockTradingEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;

public class TradingServer {
    public static void main(String[] args) {
        OrderBook orderBook = new OrderBook();
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while(true){
                Socket clienSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clienSocket, orderBook);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    private static class ClientHandler implements Runnable {
        private Socket serverSocket;
        private OrderBook orderBook;

        public ClientHandler(Socket clienSocket, OrderBook orderBook){
            this.orderBook = orderBook;
            this.serverSocket = clienSocket;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                PrintWriter out = new PrintWriter(serverSocket.getOutputStream(),true);
                String inputLine;
                while((inputLine = in.readLine()) != null){
                    String [] parts = inputLine.split(" ");
                    String type = parts[0];
                    String company = parts[1];
                    int qty = Integer.parseInt(parts[2]);
                    double price = Double.parseDouble(parts[3]);
                    Order newOrder;
                    if(type.equals("BUY")){
                        newOrder = new BuyOrder(company, qty, price, "TRADER");
                    }else{
                        newOrder = new SellOrder(company, qty, price, "TRADER");
                    }
                    orderBook.addOrder(newOrder);
                    out.println("Order Received: " + type + " " + company);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 
    }
}
