package src;

import java.util.concurrent.ConcurrentHashMap;


public class ClientManager {
    
    private static ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    // Registration
    public static void addClient(String username, ClientHandler handler) {
        clients.put(username, handler);
        System.out.println("User " + username + " has joined.");
    }

    // Lookup
    public static ClientHandler getHandler(String username) {
        return clients.get(username);
    }

    // Cleanup
    public static void removeClient(String username) {
        clients.remove(username);
        System.out.println("User " + username + " has disconnected");
    }
    

    public static boolean isOnline(String username) {
        return clients.containsKey(username);
    }
}