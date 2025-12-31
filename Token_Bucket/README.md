# Token Bucket Rate Limiter

A Java implementation of the Token Bucket algorithm for rate limiting API requests or controlling resource access.

## Overview

The Token Bucket algorithm is a popular rate limiting technique that allows for controlled bursts of activity while maintaining an average rate limit over time. This implementation provides:

- **Burst capacity**: Allow up to N requests instantly when tokens are available
- **Sustained rate**: Refill tokens at a configurable rate (tokens per second)
- **Per-user rate limiting**: Each user gets their own token bucket
- **Thread-safe**: Uses synchronized methods for concurrent access

## How Token Bucket Works

```
┌─────────────────┐
│   Token Bucket  │  ← Refills at rate R tokens/second
│  [🪙🪙🪙🪙🪙]   │  ← Current tokens (max capacity C)
└─────────────────┘
        ↓
   Request comes in
        ↓
   Token available? 
        ↓
   YES → Remove token → Allow request
   NO  → Reject request
```

1. **Bucket Capacity**: Maximum number of tokens the bucket can hold (burst size)
2. **Refill Rate**: How many tokens are added per second 
3. **Token Consumption**: Each request consumes one token
4. **Request Handling**: 
   - If tokens available → consume token, allow request
   - If no tokens → reject request

## Quick Start

### Compile and Run
```bash
# Compile all classes
javac Token_Bucket\*.java

# Run the demo
java Token_Bucket.Main
```

### Expected Output
```
--- Starting Burst Test (Capacity 10) ---
Request 1 allowed: true
Request 2 allowed: true
Request 3 allowed: true
Request 4 allowed: true
Request 5 allowed: true
Request 6 allowed: true
Request 7 allowed: true
Request 8 allowed: true
Request 9 allowed: true
Request 10 allowed: true
Request 11 allowed: false
Request 12 allowed: false
Request 13 allowed: false
Request 14 allowed: false
Request 15 allowed: false

--- Waiting 1 Second for Refill ---
Request after refill allowed: true
```

## Architecture

### Classes

1. **TokenBucket** - Core rate limiting algorithm implementation
2. **RateLimiterService** - Service layer that manages per-user token buckets
3. **Main** - Demo application showing burst behavior and refill

### Configuration Parameters

The current configuration (in `RateLimiterService.access()`):
- **Max Capacity**: 10 tokens
- **Initial Tokens**: 10 tokens (bucket starts full)
- **Refill Rate**: 1 token per second

## Usage Examples

### Basic Usage
```java
// Create rate limiter service
RateLimiterService rateLimiter = new RateLimiterService(new ConcurrentHashMap<>());

// Check if user can make request
boolean allowed = rateLimiter.access("user123");
if (allowed) {
    // Process request
    processRequest();
} else {
    // Reject request - rate limit exceeded
    sendRateLimitError();
}
```

### Custom Token Bucket
```java
// Create custom bucket: 5 capacity, 5 initial tokens, 2 tokens/second refill
TokenBucket customBucket = new TokenBucket(5, 5, 2);

// Try to consume token
boolean success = customBucket.tryConsume();
```

### Different Rate Limits per User Type
```java
public class CustomRateLimiterService {
    private ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    
    public boolean accessPremiumUser(String userId) {
        TokenBucket bucket = buckets.computeIfAbsent(userId, 
            k -> new TokenBucket(100, 100, 10)); // Higher limits
        return bucket.tryConsume();
    }
    
    public boolean accessFreeUser(String userId) {
        TokenBucket bucket = buckets.computeIfAbsent(userId, 
            k -> new TokenBucket(10, 10, 1)); // Lower limits  
        return bucket.tryConsume();
    }
}
```

## Implementation Details

### Token Refill Algorithm
```java
long timeSinceLastRefill = System.nanoTime() - lastRefillTimestamp;
long tokensToAdd = (timeSinceLastRefill * refillRate) / 1_000_000_000;

if (tokensToAdd > 0) {
    currentTokens = Math.min(maxCapacity, currentTokens + (int)tokensToAdd);
    lastRefillTimestamp = System.nanoTime();
}
```

### Key Features
- **Precision Timing**: Uses `System.nanoTime()` for accurate time measurement
- **Overflow Protection**: `Math.min()` prevents exceeding bucket capacity
- **Thread Safety**: `synchronized` methods prevent race conditions
- **Lazy Refill**: Tokens only calculated when `tryConsume()` is called

## Testing Scenarios

### Burst Test (Current Demo)
Tests the ability to handle burst requests up to capacity limit.

### Sustained Rate Test
```java
// Test sustained rate over time
for (int i = 0; i < 60; i++) {
    boolean allowed = rateLimiter.access("user1");
    System.out.println("Second " + i + ": " + allowed);
    Thread.sleep(1000); // Wait 1 second between requests
}
```

### Concurrent User Test
```java
// Test multiple users simultaneously
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 100; i++) {
    final int userId = i % 5; // 5 different users
    executor.submit(() -> {
        boolean allowed = rateLimiter.access("user" + userId);
        System.out.println("User " + userId + ": " + allowed);
    });
}
```

## Common Use Cases

1. **API Rate Limiting**: Prevent abuse of REST APIs
2. **Database Connection Throttling**: Control concurrent DB access
3. **Message Queue Processing**: Limit message consumption rate
4. **Resource Access Control**: Throttle expensive operations
5. **DDoS Protection**: Limit requests per IP address

## Advantages of Token Bucket

- ✅ **Allows Bursts**: Handle traffic spikes within capacity
- ✅ **Smooth Rate Control**: Maintains average rate over time  
- ✅ **Fair Distribution**: Each user gets independent rate limit
- ✅ **Memory Efficient**: Only stores buckets for active users
- ✅ **Configurable**: Easily adjust capacity and refill rate

## Limitations

- ❌ **Memory Usage**: Stores state per user (can grow large)
- ❌ **No Persistence**: Buckets reset on application restart
- ❌ **Clock Dependency**: Relies on system time accuracy

## Future Enhancements

- **Persistent Storage**: Save bucket state to database/Redis
- **Distributed Rate Limiting**: Share state across multiple servers
- **Advanced Algorithms**: Implement sliding window or leaky bucket
- **Metrics Collection**: Track rate limit hits and patterns
- **Configuration Management**: External config files for rate limits
- **Cleanup Strategy**: Remove inactive user buckets to save memory

## Dependencies

- Java 8 or higher
- `java.util.concurrent.ConcurrentHashMap` for thread-safe storage
- No external dependencies required

## Performance Considerations

- **Time Complexity**: O(1) for `tryConsume()` operation
- **Space Complexity**: O(U) where U is number of active users
- **Thread Safety**: Synchronized methods may cause contention under high load
- **Memory Cleanup**: Consider implementing user bucket expiration

## License

This project is for educational purposes.