package src;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String myUsername = null; 
    private boolean isRunning = true;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            // Setup Streams
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("New connection started...");
            while (isRunning) {
                Protocol.Header header = Protocol.readHeaderFromStream(in);
                if (header == null || !Protocol.isValidMagic(header.magic)) {
                    System.out.println("Invalid header or connection lost.");
                    break;
                }
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
        switch(header.command){
            case Protocol.CMD_LOGIN:
                this.myUsername = payload;
                ClientManager.addClient(myUsername, this);
                System.out.println(myUsername + " is now logged in.");
                break;
            case Protocol.CMD_MSG:
                String[] parts = payload.split("\\|", 2);
                if (parts.length == 2) {
                    String targetUser = parts[0]; 
                    String messageBody = parts[1];
                    ClientHandler targetHandler = ClientManager.getHandler(targetUser);
                    System.out.println("DEBUG: Forwarding msg from " + myUsername + " to " + targetUser);
                    if (targetHandler != null) {
                        targetHandler.sendMessage((byte)Protocol.CMD_MSG, this.myUsername + "|" + messageBody);
                        System.out.println("DEBUG: Message sent to output stream.");
                    } else {
                        System.out.println("DEBUG: User " + targetUser + " NOT FOUND in map.");
                    }
                }
                break;
            // More cases here later (like ACK or LOGOUT)
            case Protocol.CMD_USER_LIST:
                System.out.println("DEBUG: User " + myUsername + " asked for user list.");
                String usersList = getUsers();
                send_users_list(usersList);
                break;
            default:
                System.out.println("Unknown command received: " + header.command);
                break;
        }    
    }
    public void sendMessage(byte cmdId, String text) {
        Protocol.sendMessage(out, cmdId, 0, text);
    }
    
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
    public void send_users_list(String usersList) {
        Protocol.sendMessage(out, (byte)Protocol.CMD_USER_LIST, 0, usersList);
    }

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