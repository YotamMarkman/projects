package Token_Bucket;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterService {
    private ConcurrentHashMap<String, TokenBucket> map;

    public RateLimiterService(ConcurrentHashMap<String, TokenBucket> map){
        this.map = map;
    }
    public boolean access(String userId){
        TokenBucket bucket = map.computeIfAbsent(userId, k -> new TokenBucket(10, 10, 1));
        return bucket.tryConsume();
    }
}
