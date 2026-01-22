package src;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Handles a single client connection - each client gets their own thread running this
 */
public class ClientHandler implements Runnable {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String myUsername = null;  // Who am I talking to?
    private boolean isRunning = true;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try {
            // Get the input/output streams to read and write data
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("New connection started...");
            
            // Keep reading messages until the client disconnects
            while (isRunning) {
                // Read the 19-byte header first
                Protocol.Header header = Protocol.readHeaderFromStream(in);
                if (header == null || !Protocol.isValidMagic(header.magic)) {
                    System.out.println("Invalid header or connection lost.");
                    break;
                }
                
                // Now read the actual message content
                String payload = Protocol.readPayloadFromStream(in, header.length);
                handleCommand(header, payload);
            }

        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void handleCommand(Protocol.Header header, String payload) {
        // Debug spam to see what we're receiving
        System.out.println("[DEBUG] Received command byte: 0x" + String.format("%02X", header.command));
        System.out.println("[DEBUG] Command as int: " + (int)header.command);
        System.out.println("[DEBUG] Payload: " + payload);
        System.out.println("[DEBUG] CMD_MY_GROUPS constant = " + Protocol.CMD_MY_GROUPS);
        System.out.println("[DEBUG] Are they equal? " + (header.command == Protocol.CMD_MY_GROUPS));
        
        switch(header.command){
            case Protocol.CMD_LOGIN:
                // First message from client - they're telling us their username
                this.myUsername = payload;
                ClientManager.addClient(myUsername, this);
                System.out.println(myUsername + " is now logged in.");
                break;

            case Protocol.CMD_MSG:
                // Private message format: "targetUser|message"
                String[] parts = payload.split("\\|", 2);
                if (parts.length == 2) {
                    String targetUser = parts[0]; 
                    String messageBody = parts[1];
                    
                    // Look up the target user's handler
                    ClientHandler targetHandler = ClientManager.getHandler(targetUser);
                    System.out.println("DEBUG: Forwarding msg from " + myUsername + " to " + targetUser);
                    
                    if (targetHandler != null) {
                        // Send it with the sender's name prepended
                        targetHandler.sendMessage((byte)Protocol.CMD_MSG, this.myUsername + "|" + messageBody);
                        System.out.println("DEBUG: Message sent to output stream.");
                    } else {
                        System.out.println("DEBUG: User " + targetUser + " NOT FOUND in map.");
                    }
                }
                break;

            case Protocol.CMD_USER_LIST:
                // Client wants to see who's online
                System.out.println("DEBUG: User " + myUsername + " asked for user list.");
                String usersList = getUsers();
                send_users_list(usersList);
                break;

            case Protocol.CMD_CREATE_GROUP:
                // Format: "groupName|user1,user2,user3"
                String[] groupParts = payload.split("\\|", 2);
                if(groupParts.length == 2){
                    String groupName = groupParts[0];
                    String groupMembers = groupParts[1];
                    
                    // Split comma-separated members into a list
                    String[] members = groupMembers.split(",");
                    ArrayList<String> peopleInGroup = new ArrayList<>();
                    for (String name : members){
                        peopleInGroup.add(name);
                    }
                    
                    ClientManager.createGroup(groupName, peopleInGroup);
                    System.out.println("DEBUG: Created Group " + groupName);
                }
                break;

            case Protocol.CMD_GROUP_MSG:
                // Format: "groupName|message"
                String[] groupMsgParts = payload.split("\\|", 2);
                if (groupMsgParts.length == 2) {
                    String group_Name = groupMsgParts[0];
                    String messageToGroup = groupMsgParts[1];
                    
                    // Get all members of this group
                    ArrayList<String> members = ClientManager.getGroupMembers(group_Name);

                    if (members != null) {
                        // Send to everyone in the group except the sender
                        for(String mem : members){
                            if(mem.equals(this.myUsername)) continue;  // Skip myself

                            ClientHandler targetHandler = ClientManager.getHandler(mem);
                            if (targetHandler != null) {
                                String forwardPayload = group_Name + "|" + this.myUsername + ": " + messageToGroup;
                                targetHandler.sendMessage((byte)Protocol.CMD_GROUP_MSG, forwardPayload);
                            }
                        }
                    } else {
                        // Oops, group doesn't exist
                        System.out.println("DEBUG: Group " + group_Name + " does not exist.");
                        sendMessage((byte)Protocol.CMD_ERROR, "Group " + group_Name  + " does not exist try again" );
                    }
                }
                break;

            case Protocol.CMD_MY_GROUPS:
                // Get all groups this user is in
                ArrayList<String> groupsOfUser = ClientManager.getUserGroups(this.myUsername);
                String result = groupsOfUser.toString().replace("[", "").replace("]", ""); 
                sendMessage((byte)Protocol.CMD_MY_GROUPS, result);
                break;

            default:
                System.out.println("Unknown command received: " + header.command);
                break;
        }    
    }
    
    // Quick wrapper to send a message to this client
    public void sendMessage(byte cmdId, String text) {
        Protocol.sendMessage(out, cmdId, 0, text);
    }
    
    // Build a comma-separated list of all online users
    public String getUsers(){
        StringBuilder usersList = new StringBuilder();
        for(Object user : ClientManager.getAllClients()){
            if(usersList.length() > 0){
                usersList.append(",");
            }
            usersList.append((String)user);
        }
        return usersList.toString();
    }
    
    // Send the user list as a response
    public void send_users_list(String usersList) {
        Protocol.sendMessage(out, (byte)Protocol.CMD_USER_LIST, 0, usersList);
    }

    // Clean up when the client disconnects
    private void closeConnection() {
        if (myUsername != null) {
            ClientManager.removeClient(myUsername);
        }
        try {
            socket.close();
            System.out.println("Closed " + myUsername +  " socket");
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }
}