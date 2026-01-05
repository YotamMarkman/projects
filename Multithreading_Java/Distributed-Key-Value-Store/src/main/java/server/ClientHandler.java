/*  3. ClientHandler.java
Goal: The "Worker." This file handles the conversation with one specific client for the duration of their session. This class must implement Runnable so it can run on a separate thread.

What needs to be implemented:

Class Variables:

The Socket (passed from Server).

The DataStore (passed from Server).

run() method:

Setup Streams: Wrap the socket's InputStream in a BufferedReader (for reading lines) and OutputStream in a PrintWriter (for sending text).

The Loop: Enter a while loop that reads from the BufferedReader (input.readLine()) until the client disconnects (returns null).

Protocol Logic:

Read the line (e.g., "SET name John").

Split the string by spaces to get the command parts.

Use a switch statement or if/else to check the command (SET, GET, DEL).

Call the appropriate method in DataStore.

Send the result back using the PrintWriter.

Cleanup: Close the socket and streams in a finally block. */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private  Socket clientSocket;
    private  DataStore dataStore;

    //Constructor
    public ClientHandler(Socket socket, DataStore dataStore) {
        this.clientSocket = socket;
        this.dataStore = dataStore;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            String request;
            while((request = in.readLine()) != null){
                String[] parts = request.split("\\s+");
                String command = parts[0];
                switch(command) {
                    case "SET":
                        if(parts.length == 3) {
                            dataStore.set(parts[1], parts[2]);
                            out.println("OK");
                        } else {
                            out.println("ERROR: Invalid SET command");
                        }
                        break;
                    case "GET":
                        if(parts.length == 2) {
                            String value = dataStore.get(parts[1]);
                            if(value != null) {
                                out.println(value);
                            } else {
                                out.println("NULL");
                            }
                        } else {
                            out.println("ERROR: Invalid GET command");
                        }
                        break;
                    case "DEL":
                        if(parts.length == 2) {
                            boolean deleted = dataStore.delete(parts[1]);
                            out.println(deleted ? "DELETED" : "NOT FOUND");
                        } else {
                            out.println("ERROR: Invalid DEL command");
                        }
                        break;
                    default:
                        out.println("ERROR: Unknown command");
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(in != null) in.close();
                if(out != null) out.close();
                if(clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
