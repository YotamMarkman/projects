import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class TaskProcessor {
    // Implementation of TaskProcessor
    private BlockingQueue<String> q;
    private ExecutorService executorService;
    public TaskProcessor(BlockingQueue<String> queue, ExecutorService executorService){
        this.q = queue;
        this.executorService = executorService;
        startConsumer();
    }
    private void startConsumer(){
        executorService.submit(() -> {
            try{
                while (true) {
                    String task = q.take();
                    process(task);
                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt(); 
                System.out.println("Consumer thread interrupted, stopping.");
            }
        });
    }
    private void process(String task){
        System.out.println("Processing: " + task + " on thread: " + Thread.currentThread().getName());
    }
    public void sumbitTask(String task){
        try{
            q.put(task);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

    }
}