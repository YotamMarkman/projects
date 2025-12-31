import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Mock_backend {
    private int port;
    private String name;

    public Mock_backend(int port, String name) {
        this.port = port;
        this.name = name;
    }

    public void start() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println(name + " started on port " + port);
                
                while (true) {
                    Socket socketClient = serverSocket.accept(); 
                    handleClient(socketClient);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start(); 
    }

    private void handleClient(Socket clientHandler) {
        try {
            InputStream input = clientHandler.getInputStream();
            OutputStream output = clientHandler.getOutputStream();
            
            byte[] buffer = new byte[1024];
            int bytesRead = input.read(buffer);
            if (bytesRead > 0) {
                String request = new String(buffer, 0, bytesRead);
                System.out.println(name + " received request: " + request);
                
                String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + name + " response";
                output.write(httpResponse.getBytes("UTF-8"));
            }
            clientHandler.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}