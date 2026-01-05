/*  2. Server.java
Goal: The "Receptionist." It listens for connections and assigns them to workers. It should never block on actual data processing; its only job is to accept the connection.

What needs to be implemented:

Class Variables:

A ServerSocket to listen for connections.

A reference to the DataStore (to pass to workers).

A "running" boolean flag (to allow for a graceful shutdown later).

Crucial: An ExecutorService (Thread Pool).

Why: If you just use new Thread(), 10,000 users will crash your OS. A FixedThreadPool limits the active threads to a safe number (e.g., 10 or 50).

start() method:

Initialize the ServerSocket on the specific port.

Enter a while loop (infinite loop).

Inside the loop: Call serverSocket.accept(). This line pauses the code until a client connects.

When a client connects, it returns a Socket.

The Handoff: Create a new ClientHandler instance (passing the socket and data store) and submit it to the ExecutorService. */


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private DataStore dataStore;
    private boolean running;    
    private ExecutorService threadPool;

    public Server(int port, DataStore dataStore ){
        this.port = port;
        this.dataStore = dataStore;
        this.threadPool = Executors.newFixedThreadPool(10);
        this.running = true;
    }

    public void start() {
    try {
            // 1. Initialize
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            // 2. Loop (Inside the try block)
            while (running) {
                // If accept() fails, we want to catch it below
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, dataStore);
                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            // This catches errors from BOTH the constructor and accept()
            e.printStackTrace();
        }
    }
}
