package org.acme.ratelimit;

import io.github.bucket4j.Bucket;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class RateLimitService {

    private final int capacity;
    private final int refillRate;

    private final Map<String, Bucket> buckets;

    public RateLimitService(@ConfigProperty(name = "bucket.capacity") int capacity,
                            @ConfigProperty(name = "bucket.refill.rate") int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.buckets = Collections.synchronizedMap(new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Bucket> eldest) {
                Set<Map.Entry<String, Bucket>> entries = entrySet();

                synchronized (this) {
                    Iterator<Map.Entry<String, Bucket>> iterator = entries.iterator();

                    while (iterator.hasNext() && entries.size() > 1) {
                        Bucket bucket = iterator.next().getValue();

                        if (bucket.tryConsume(capacity)) {
                            iterator.remove();
                        } else {
                            break;
                        }
                    }
                }
                return false;
            }
        });
    }

    public synchronized Bucket resolveBucket(String clientKey) {
        return buckets.computeIfAbsent(clientKey, k -> buildNewBucket());
    }

    private Bucket buildNewBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(capacity).refillGreedy(capacity, Duration.ofSeconds(refillRate)))
                .build();
    }
}
