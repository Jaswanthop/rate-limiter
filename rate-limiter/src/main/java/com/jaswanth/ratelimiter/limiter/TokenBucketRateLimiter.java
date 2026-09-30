package com.jaswanth.ratelimiter.limiter;

import com.jaswanth.ratelimiter.domain.ClientIdentity;
import com.jaswanth.ratelimiter.domain.RateLimitResult;
import com.jaswanth.ratelimiter.domain.TokenBucketPolicy;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketRateLimiter {

    private final Map<String, Bucket> buckets =
            new ConcurrentHashMap<>();

    public RateLimitResult check(
            ClientIdentity client,
            TokenBucketPolicy policy
    ) {

        Bucket bucket =
                buckets.computeIfAbsent(
                        client.value(),
                        key -> new Bucket(
                                policy.capacity()
                        )
                );

        synchronized (bucket) {

            refill(bucket, policy);

            if (bucket.tokens >= 1.0) {

                bucket.tokens--;

                return new RateLimitResult(
                        true,
                        policy.capacity(),
                        (int) bucket.tokens,
                        0
                );
            }

            long retryAfter =
                    (long) Math.ceil(
                            1.0 / policy.refillRate()
                    );

            return new RateLimitResult(
                    false,
                    policy.capacity(),
                    0,
                    retryAfter
            );
        }
    }

    private void refill(
            Bucket bucket,
            TokenBucketPolicy policy
    ) {

        long now =
                Instant.now().toEpochMilli();

        long elapsedMillis =
                now - bucket.lastRefillTime;

        double elapsedSeconds =
                elapsedMillis / 1000.0;

        double tokensToAdd =
                elapsedSeconds * policy.refillRate();

        bucket.tokens =
                Math.min(
                        policy.capacity(),
                        bucket.tokens + tokensToAdd
                );

        bucket.lastRefillTime = now;
    }

    private static class Bucket {

        private double tokens;

        private long lastRefillTime;

        private Bucket(int capacity) {
            this.tokens = capacity;
            this.lastRefillTime =
                    Instant.now().toEpochMilli();
        }
    }
}