package src;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry for all connected clients and groups
 * Thread-safe because multiple ClientHandlers might access this at once
 */
public class ClientManager {
    
    // Map username -> their handler (so we can send them messages)
    private static ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
    
    // Map group name -> list of members
    private static ConcurrentHashMap<String, ArrayList<String>> groups = new ConcurrentHashMap<>();

    // Add a new client when they log in
    public static void addClient(String username, ClientHandler handler) {
        clients.put(username, handler);
        System.out.println("User " + username + " has joined.");
    }

    // Find a specific user's handler (for sending messages)
    public static ClientHandler getHandler(String username) {
        return clients.get(username);
    }

    // Remove a client when they disconnect
    public static void removeClient(String username) {
        clients.remove(username);
        System.out.println("User " + username + " has disconnected");
    }
    
    // Get all currently connected usernames
    public static List getAllClients() {
        List<String> userList = new ArrayList<>();
        for (String username : clients.keySet()) {
            userList.add(username);
        }
        return userList;
    }

    // Check if someone is online
    public static boolean isOnline(String username) {
        return clients.containsKey(username);
    }

    // --- GROUP STUFF ---
    
    // Create a new group with the given members
    public static void createGroup(String groupName, ArrayList<String> members) {
        groups.put(groupName, members);
        System.out.println("Group " + groupName + " created with members: " + members);
    }
    
    // Get all members of a group
    public static ArrayList<String> getGroupMembers(String groupName) {
        return groups.get(groupName);
    }

    // Find all groups that a specific user is in
    public static ArrayList<String> getUserGroups(String username) {
        ArrayList<String> groups_of_username = new ArrayList<>();
        for(String name : groups.keySet()){
            for(String people : groups.get(name)){
                if(people.equals(username)){
                    groups_of_username.add(name);
                    break;  // Found them in this group, move to next group
                }
            }
        }
        return groups_of_username;
    }
}