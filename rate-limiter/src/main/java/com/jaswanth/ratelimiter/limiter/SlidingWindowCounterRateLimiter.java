package com.jaswanth.ratelimiter.limiter;

import com.jaswanth.ratelimiter.domain.ClientIdentity;
import com.jaswanth.ratelimiter.domain.RateLimitPolicy;
import com.jaswanth.ratelimiter.domain.RateLimitResult;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowCounterRateLimiter
        implements RateLimiter {

    private final Map<String, WindowCounter> counters =
            new ConcurrentHashMap<>();

    @Override
    public RateLimitResult check(
            ClientIdentity client,
            RateLimitPolicy policy
    ) {

        long now = Instant.now().getEpochSecond();

        long windowSize = policy.windowSeconds();

        long currentWindow =
                now / windowSize;

        long elapsed =
                now % windowSize;

        double currentWindowRatio =
                (double) elapsed / windowSize;

        double previousWindowRatio =
                1.0 - currentWindowRatio;

        WindowCounter counter =
                counters.computeIfAbsent(
                        client.value(),
                        key -> new WindowCounter(currentWindow)
                );

        synchronized (counter) {

            if (counter.windowId != currentWindow) {

                counter.previousCount =
                        counter.currentCount;

                counter.currentCount = 0;

                counter.windowId =
                        currentWindow;
            }

            double estimatedCount =
                    counter.previousCount
                            * previousWindowRatio
                            + counter.currentCount;

            if (estimatedCount >= policy.limit()) {

                return new RateLimitResult(
                        false,
                        policy.limit(),
                        0,
                        windowSize - elapsed
                );
            }

            counter.currentCount++;

            int remaining =
                    Math.max(
                            0,
                            policy.limit()
                                    - (int) Math.ceil(
                                    estimatedCount + 1
                            )
                    );

            return new RateLimitResult(
                    true,
                    policy.limit(),
                    remaining,
                    0
            );
        }
    }

    private static class WindowCounter {

        private long windowId;

        private long previousCount;

        private long currentCount;

        private WindowCounter(long windowId) {
            this.windowId = windowId;
        }
    }
}