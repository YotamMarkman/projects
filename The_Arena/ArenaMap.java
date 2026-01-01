package The_Arena;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Point; // Java has a built-in Point class (x, y)

public class ArenaMap {
    public static Object arenaMap;
    private final int SIZE = 10;
    // Maps "PlayerName" -> (x, y)
    private ConcurrentHashMap<String, Point> positions = new ConcurrentHashMap<>();

    public synchronized void addPlayer(String name) {
        // Spawn at a random spot (e.g., 5,5 for now, or random)
        positions.put(name, new Point(0, 0));
    }

    public synchronized void removePlayer(String name) {
        Point p = positions.get(name);
        positions.remove(name); 
    }

    public synchronized void movePlayer(String name, String direction) {
        Point p = positions.get(name);
        if (p == null) return;

        // TODO: Update p.x or p.y based on direction ("w", "a", "s", "d")
        // REMEMBER: (0,0) is Top-Left. 
        // "w" (Up) means y decreases. "s" (Down) means y increases.
        // Check boundaries! Don't let them go < 0 or >= SIZE.
        
        // YOUR CODE HERE
        if (direction.equals("w")) {
            if (p.y > 0) p.y--; 
        } else if (direction.equals("a")) {
            if (p.x > 0) p.x--; 
        } else if (direction.equals("s")) {
            if (p.y < SIZE - 1) p.y++; // FIX: Check against SIZE - 1
        } else if (direction.equals("d")) {
            if (p.x < SIZE - 1) p.x++; // FIX: Check against SIZE - 1
        }else{
            System.out.println("Not a direction choose again");
        }
    }

    public synchronized String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("   0 1 2 3 4 5 6 7 8 9\r\n"); // Header
        
        for (int y = 0; y < SIZE; y++) {
            sb.append(y + "  "); // Print Row Number (0-9) on the left
            
            for (int x = 0; x < SIZE; x++) {
                // Check: Is there a player at THIS specific (x, y)?
                String playerAtSpot = null;
                
                for (String name : positions.keySet()) {
                    Point p = positions.get(name);
                    if (p.x == x && p.y == y) {
                        playerAtSpot = name;
                        break; // Found one! Stop looking.
                    }
                }
                
                if (playerAtSpot != null) {
                    // Found a player! Print their first letter
                    sb.append(playerAtSpot.charAt(0) + " ");
                } else {
                    // Empty spot
                    sb.append(". ");
                }
            }
            sb.append("\r\n"); // New line at end of row
        }
        
        return sb.toString();
    }

    public synchronized Point getPosition(String name){
        return positions.get(name);
    }
    public synchronized Map<String, Point> getAllPositions(){
        Map<String, Point> allPositions = new HashMap<>();
        for(String name: positions.keySet()){
            Point p = positions.get(name);
            allPositions.put(name, p);
        }
        return allPositions;
    }

    public synchronized int getSize(){
        return SIZE;
    }
}