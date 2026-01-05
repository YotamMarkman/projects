import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancerMap {
    private AtomicInteger counter = new AtomicInteger(0);
    private String[] backends;
    private int length;
    public LoadBalancerMap(String[] backends){
        this.backends = backends;
        this.length = backends.length;
    }
    public int getNextBackend(){
        int now = Math.abs(counter.getAndIncrement());
        int index = now % length;
        System.out.print("Printing on port: " + backends[index]);
        return Integer.parseInt(backends[index]);
    }
}
