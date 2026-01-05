package MultiUserChatRoom;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        try {
            // 1. Connect to the Server
            Socket socket = new Socket("localhost", 5000);
            
            // 2. THE EAR (Background Thread)
            // This thread's job is to read from the socket and print to screen
            new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                         // TODO: Print 'serverMessage' to the console
                        System.out.println("Message : " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // 3. THE MOUTH (Main Thread)
            // This stays here and waits for you to type
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
            
            while (true) {
                String myMessage = scanner.nextLine();
                // TODO: Send 'myMessage' to the server
                ChatServer chatServer = new ChatServer();
                out.println(myMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}