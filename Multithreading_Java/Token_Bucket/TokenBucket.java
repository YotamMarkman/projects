package Token_Bucket;

public class TokenBucket {
    private int maxCapacity;
    private int currentTokens;
    private long lastRefillTimestamp;
    private int refillRate;
    public TokenBucket(int maxCapacity, int currentTokens, int refillRate) {
        this.maxCapacity = maxCapacity;
        this.currentTokens = currentTokens;
        this.refillRate = refillRate;
        this.lastRefillTimestamp = System.nanoTime();
    }
    public synchronized boolean tryConsume() {
        long timeSinceLastRefill = System.nanoTime() - lastRefillTimestamp;
        long tokensToAdd = (timeSinceLastRefill * refillRate) /1_000_000_000;
        if(tokensToAdd > 0){
            currentTokens = Math.min(maxCapacity, currentTokens + (int)tokensToAdd);
            lastRefillTimestamp = System.nanoTime();
        }
        if(currentTokens >  0){
            currentTokens--;
            return true;
        }else{
            return false;
        }
    }
}
