package The_Arena;

public class Main {
    public static void main(String[] args) {
        ArenaServer arenaServer = new ArenaServer(8080);
        arenaServer.start();
    }
}
