package CustomThreadPoolExecutor;

public class Main {
    public static void main(String[] args) {
        // 1. Start the Pool with 2 threads
        CustomThreadPool pool = new CustomThreadPool(2);
        
        System.out.println("Submitting tasks...");

        // 2. Submit heavy work (Finding 20,000th prime is slow!)
        CustomFuture<Long> future1 = pool.submit(new PrimeTask(20000));
        CustomFuture<Long> future2 = pool.submit(new PrimeTask(5000)); // Smaller task
        
        System.out.println("Tasks submitted! Main thread is free to do other things.");
        
        // 3. Now we block and wait for results
        System.out.println("Result 1: " + future1.get()); // Freezes here
        System.out.println("Result 2: " + future2.get());
    }
}