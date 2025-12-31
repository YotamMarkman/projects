import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadBalancerServer {
    public static final int LOAD_BALANCER_PORT = 8080;

    public static void main(String[] args) {
        // Backend ports must match the ports started by Main.java (Mock_backend instances)
        String[] backendPorts = {"8081", "8082"};
        LoadBalancerMap lbMap = new LoadBalancerMap(backendPorts);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(LOAD_BALANCER_PORT)) {
            System.out.println("Load Balancer started on port " + LOAD_BALANCER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Use round-robin to pick a backend port
                int backendPort = lbMap.getNextBackend();
                System.out.println("\nForwarding request to backend on port " + backendPort);
                executor.submit(new ClientHandler(clientSocket, backendPort));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
