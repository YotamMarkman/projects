package Simple_Designs;
import java.util.LinkedList;
import java.util.Queue;

public class DispatchQueue {

    private Queue<String> requests;
    private int capacity;

    public DispatchQueue(int capacity){
        this.requests = new LinkedList<>();
        this.capacity = capacity;
    }
    
    public synchronized void addRequest(String request) {
        while (requests.size() == capacity) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        requests.add(request);
        System.out.println("Rider added request: " + request);
        notifyAll(); 
    }
    public synchronized String getNextRide(){
        while(requests.isEmpty()){
            try {
                System.out.println("Queue Empty");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
            String request = requests.poll();
            notifyAll();
            return request;
        }
    }