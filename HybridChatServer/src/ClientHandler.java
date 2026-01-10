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
                    if (targetHandler != null) {
                        targetHandler.sendMessage((byte)Protocol.CMD_MSG, this.myUsername + "|" + messageBody);
                    } else {
                        System.out.println("User " + targetUser + " not found.");
                    }
                }
                break;
            // More cases here later (like ACK or LOGOUT)
            default:
                System.out.println("Unknown command received: " + header.command);
                break;
        }    
    }
    public void sendMessage(byte cmdId, String text) {
        Protocol.sendMessage(out, cmdId, 0, text);
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