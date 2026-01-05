import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private int backendPort;

    public ClientHandler(Socket clientSocket, int backendPort) {
        this.clientSocket = clientSocket;
        this.backendPort = backendPort;
    }

    @Override
    public void run() {
        Socket backendSocket = null;

        try {
            // Connect to the Backend Server
            backendSocket = new Socket("localhost", backendPort);

            // Setup the Streams
            InputStream clientIn = clientSocket.getInputStream();
            OutputStream clientOut = clientSocket.getOutputStream();
            InputStream backendIn = backendSocket.getInputStream();
            OutputStream backendOut = backendSocket.getOutputStream();

            // Use two threads for full-duplex forwarding
            final Socket finalBackendSocket = backendSocket;

            // Thread 1: Client -> Backend
            Thread clientToBackend = new Thread(() -> {
                try {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = clientIn.read(buffer)) != -1) {
                        backendOut.write(buffer, 0, bytesRead);
                        backendOut.flush();
                    }
                    // Signal end of request by shutting down output to backend
                    finalBackendSocket.shutdownOutput();
                } catch (Exception e) {
                    // Connection closed or error
                }
            });

            // Thread 2: Backend -> Client
            Thread backendToClient = new Thread(() -> {
                try {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = backendIn.read(buffer)) != -1) {
                        clientOut.write(buffer, 0, bytesRead);
                        clientOut.flush();
                    }
                } catch (Exception e) {
                    // Connection closed or error
                }
            });

            clientToBackend.start();
            backendToClient.start();

            // Wait for both directions to finish
            clientToBackend.join();
            backendToClient.join();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cleanup
            try {
                if (clientSocket != null) clientSocket.close();
                if (backendSocket != null) backendSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
