package Token_Bucket;

import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main (String[] args) throws InterruptedException {    
        RateLimiterService rateLimiterService = new RateLimiterService(new ConcurrentHashMap<>());
        
        System.out.println("--- Starting Burst Test (Capacity 10) ---");
        
        // 2. Loop 15 times
        for (int i = 1; i <= 15; i++) {
            boolean allowed = rateLimiterService.access("user1");
            
            System.out.println("Request " + i + " allowed: " + allowed);
        }
        
        System.out.println("\n--- Waiting 1 Second for Refill ---");
        Thread.sleep(1000);
        
        boolean allowedAfterRefill = rateLimiterService.access("user1");
        System.out.println("Request after refill allowed: " + allowedAfterRefill);
    }
}