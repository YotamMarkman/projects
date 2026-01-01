package The_Arena;

import java.awt.Point;
import java.util.Map;

public class BossMonster implements Runnable {
    private String name;

    public BossMonster(String name){
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println(name + " has entered The Arena!");
        ArenaServer.arenaMap.addPlayer(this.name);
        
        while (true) {
            try {
                Thread.sleep(2000);
                
                Point myPos = ArenaServer.arenaMap.getPosition(this.name);
                if (myPos == null) continue; 

                Map<String, Point> allPositions = ArenaServer.arenaMap.getAllPositions();
                
                Point targetPos = null;
                int minDistance = Integer.MAX_VALUE; 

                for (String playerName : allPositions.keySet()) {
                    if (playerName.equals(this.name)) continue;

                    Point playerPos = allPositions.get(playerName);
                    int distance = Math.abs(myPos.x - playerPos.x) + Math.abs(myPos.y - playerPos.y);
                    
                    if (distance < minDistance) {
                        minDistance = distance;
                        targetPos = playerPos;
                    }
                }

                if (targetPos != null) {
                    String direction = "";
                    
                    // Simple logic: Move horizontally first, then vertically
                    if (myPos.x < targetPos.x) {
                        direction = "d"; // Right
                    } else if (myPos.x > targetPos.x) {
                        direction = "a"; // Left
                    } else if (myPos.y < targetPos.y) {
                        direction = "s"; // Down
                    } else if (myPos.y > targetPos.y) {
                        direction = "w"; // Up
                    }
                    
                    if (!direction.isEmpty()) {
                        System.out.println("BOSS IS MOVING: " + direction);
                        ArenaServer.arenaMap.movePlayer(this.name, direction);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }   
}