package com.jaswanth.ratelimiter.limiter;

import com.jaswanth.ratelimiter.domain.ClientIdentity;
import com.jaswanth.ratelimiter.domain.RateLimitPolicy;
import com.jaswanth.ratelimiter.domain.RateLimitResult;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowLogRateLimiter implements RateLimiter {

    private final Map<String, Deque<Long>> requestLogs =
            new ConcurrentHashMap<>();

    @Override
    public RateLimitResult check(
            ClientIdentity client,
            RateLimitPolicy policy
    ) {

        long now = Instant.now().getEpochSecond();

        long windowStart =
                now - policy.windowSeconds();

        Deque<Long> timestamps =
                requestLogs.computeIfAbsent(
                        client.value(),
                        key -> new ArrayDeque<>()
                );

        synchronized (timestamps) {

            while (!timestamps.isEmpty()
                    && timestamps.peekFirst() <= windowStart) {

                timestamps.pollFirst();
            }

            if (timestamps.size() >= policy.limit()) {

                long oldestTimestamp =
                        timestamps.peekFirst();

                long retryAfter =
                        (oldestTimestamp
                                + policy.windowSeconds())
                                - now;

                return new RateLimitResult(
                        false,
                        policy.limit(),
                        0,
                        Math.max(retryAfter, 0)
                );
            }

            timestamps.addLast(now);

            return new RateLimitResult(
                    true,
                    policy.limit(),
                    policy.limit() - timestamps.size(),
                    0
            );
        }
    }
}
