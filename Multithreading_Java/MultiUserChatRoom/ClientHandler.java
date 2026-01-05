package MultiUserChatRoom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {    
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            while((message = in.readLine()) != null){
                System.out.println("Recieved " + message);
                ChatServer.broadcast(message);

            }
        } catch (IOException e ){
            e.printStackTrace();
        } finally{
            try{
                socket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
    
