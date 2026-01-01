package The_Arena;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArenaServer {
    private int port;
    List<ClientHandler> clients;
    public static ArenaMap arenaMap;

    public ArenaServer(int port){
        this.port = port;
        this.clients = new CopyOnWriteArrayList<>();
        arenaMap = new ArenaMap();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Arena Server is running on port " + port);

            new Thread(new BossMonster("BOSS")).start();;

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New Warrior Connected!");
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port " + port);
            e.printStackTrace();
        }
    }
}
