package CustomThreadPoolExecutor;

import java.util.concurrent.LinkedBlockingQueue;

public class CustomThreadPool {
    LinkedBlockingQueue<Runnable> taskQueue;
    public CustomThreadPool(int nThreads){
        taskQueue = new LinkedBlockingQueue<>();
        for(int i = 0; i < nThreads; i++){
            System.out.println("Thread " + (i+1) + " is activated");
            new Thread(() -> {
                while (true) {
                    try {
                        Runnable task = taskQueue.take();
                        task.run();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                }
            }).start();
        }
    }
    public <T> CustomFuture<T> submit(Task<T> task){
        CustomFuture<T> future = new CustomFuture<>();
        Runnable wrapper = () -> {
            T result = task.execute();
            future.complete(result);
        };
        taskQueue.add(wrapper);
        return future;
    }
    
}
