package src;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main server entry point - fires up the chat server and handles incoming connections
 */
public class ServerMain {
    public static void main(String[] args) {
        int port = 8080;
        
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("The Hybird Chat Server is running on port " + port);

            while (true) {
                // Wait for someone to connect (this blocks until a client shows up)
                Socket clientSocket = server.accept();
                
                // Spin up a handler for this specific client
                ClientHandler handler = new ClientHandler(clientSocket);
                
                // Launch it in its own thread so we can accept more connections
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}