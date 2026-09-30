package com.jaswanth.ratelimiter.limiter;



import com.jaswanth.ratelimiter.domain.ClientIdentity;
import com.jaswanth.ratelimiter.domain.RateLimitPolicy;
import com.jaswanth.ratelimiter.domain.RateLimitResult;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiter implements RateLimiter {

    private final Map<String, WindowCounter> counters =
            new ConcurrentHashMap<>();

    @Override
    public RateLimitResult check(
            ClientIdentity client,
            RateLimitPolicy policy
    ) {

        long currentWindow =
                Instant.now().getEpochSecond()
                        / policy.windowSeconds();

        String key =
                client.value() + ":" + currentWindow;

        WindowCounter counter =
                counters.computeIfAbsent(
                        key,
                        k -> new WindowCounter()
                );

        int count = counter.count.incrementAndGet();

        if (count <= policy.limit()) {

            return new RateLimitResult(
                    true,
                    policy.limit(),
                    policy.limit() - count,
                    0
            );
        }

        return new RateLimitResult(
                false,
                policy.limit(),
                0,
                policy.windowSeconds()
        );
    }

    private static class WindowCounter {

        private final AtomicInteger count =
                new AtomicInteger();
    }
}
