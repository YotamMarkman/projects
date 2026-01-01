package The_Arena;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String playerName; 

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{    
            out.println("Connected to The Arena! Enter your name:");
            playerName = in.readLine();
            System.out.println(playerName + " has joined the battle.");
            out.println("Welcome, " + playerName + ". Type 'quit' to leave.");
            
            ArenaServer.arenaMap.addPlayer(playerName);
            // FIX: Show the map immediately so they don't stare at a blank screen
            out.println(ArenaServer.arenaMap.render());
            out.println("Move (w/a/s/d):");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if ("quit".equalsIgnoreCase(inputLine)) {
                    break;
                }
                // 2. Handle Movement
                // If they type "w", "a", "s", or "d", move them!
                if ("wasd".contains(inputLine.toLowerCase())) {
                    ArenaServer.arenaMap.movePlayer(playerName, inputLine);
                }
                
                // 3. Send the updated map to the user
                String view = ArenaServer.arenaMap.render();
                out.println(view);
                out.println("Move (w/a/s/d):");
                out.println("You said: " + inputLine); 
            }

            // Cleanup when they leave
            ArenaServer.arenaMap.removePlayer(playerName);
            socket.close();
            System.out.println(playerName + " disconnected.");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
}
