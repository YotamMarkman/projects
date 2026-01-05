package CustomThreadPoolExecutor;

public class CustomFuture<T> {
    
    private T result;
    private boolean isComplete = false;
    
    public synchronized T get(){
        try {
            while(!isComplete){
                wait();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;    
    }

    public synchronized void complete(T result){
        this.result = result;
        isComplete = true;
        notifyAll(); 
    }
}
