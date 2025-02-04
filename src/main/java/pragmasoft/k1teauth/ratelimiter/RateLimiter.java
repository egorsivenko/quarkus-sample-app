package pragmasoft.k1teauth.ratelimiter;

import io.github.bucket4j.Bucket;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Singleton;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Singleton
public class RateLimiter {

    private final int capacity;
    private final Duration refillRate;

    private final Map<String, Bucket> buckets;

    public RateLimiter(@Property(name = "cache.capacity") int cacheCapacity,
                       @Property(name = "bucket.capacity") int bucketCapacity,
                       @Property(name = "bucket.refillRate") Duration bucketRefillRate) {
        this.capacity = bucketCapacity;
        this.refillRate = bucketRefillRate;
        this.buckets = Collections.synchronizedMap(new LRUCache<>(cacheCapacity));
    }

    public synchronized boolean tryAcquire(String clientKey) {
        return buckets.computeIfAbsent(clientKey, key -> buildNewBucket())
                .tryConsume(1L);
    }

    private Bucket buildNewBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(capacity).refillGreedy(capacity, refillRate))
                .build();
    }
}
