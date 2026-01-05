package MultiUserChatRoom;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {
    private static List<PrintWriter> allClients = Collections.synchronizedList(new ArrayList<>());

    public static synchronized void broadcast(String message){
        for(PrintWriter writer : allClients){
            writer.println(message);
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Chat Server started on Port 5000");

            while (true) {
                // Wait for a guest to arrive
                Socket socket = serverSocket.accept();
                System.out.println("A new client connected!");

                // Create the "Pen" (PrintWriter) to send messages TO them
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Add them to the list (Synchronized to prevent crashes)
                synchronized (allClients) {
                    allClients.add(out);
                }

                // Start their personal thread
                ClientHandler handler = new ClientHandler(socket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
