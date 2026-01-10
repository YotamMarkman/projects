package src;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        int port = 8080;
        
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("The Hybird Chat Server is running on port " + port);

            while (true) {
                // 1. Accept new connection (Blocks until someone connects)
                Socket clientSocket = server.accept();
                
                // 2. Create the worker
                ClientHandler handler = new ClientHandler(clientSocket);
                
                // 3. Launch the thread
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}