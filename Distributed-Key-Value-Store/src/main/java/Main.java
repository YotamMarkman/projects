/* 1. Main.java
Goal: The entry point. It acts as the "Bootstrapper" that wires everything together and starts the engine.

What needs to be implemented:

Configuration: Define the port number (e.g., 6379) as a constant or read it from arguments.

Initialization:

Create the single shared instance of DataStore. (This is crucial: you create it here and pass it down so all threads share the same database).

Create an instance of Server, passing the port and the DataStore to it.

Start: Call the start() method on your server instance. */

public class Main{
    public static void main(String[] args) {
        final int PORT = 6379; // Define the port number
        DataStore dataStore = new DataStore(); // Create the shared DataStore instance
        Server server = new Server(PORT, dataStore); // Create the Server instance
        server.start(); // Start the server
    }
}