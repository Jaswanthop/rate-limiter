package com.jaswanth.rate_limiter.limiter;


import com.jaswanth.rate_limiter.domain.ClientIdentity;
import com.jaswanth.rate_limiter.domain.RateLimitPolicy;
import com.jaswanth.rate_limiter.domain.RateLimitResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FixedWindowRateLimiterTest {

    @Test
    void shouldAllowRequestsWithinLimit() {

        RateLimiter limiter =
                new FixedWindowRateLimiter();

        ClientIdentity client =
                new ClientIdentity("USER_ID", "user-1");

        RateLimitPolicy policy =
                new RateLimitPolicy(5, 10);

        for (int i = 0; i < 5; i++) {

            RateLimitResult result =
                    limiter.check(client, policy);

            assertTrue(result.allowed());
        }
    }

    @Test
    void shouldRejectRequestAfterLimit() {

        RateLimiter limiter =
                new FixedWindowRateLimiter();

        ClientIdentity client =
                new ClientIdentity("USER_ID", "user-1");

        RateLimitPolicy policy =
                new RateLimitPolicy(5, 10);

        for (int i = 0; i < 5; i++) {
            limiter.check(client, policy);
        }

        RateLimitResult result =
                limiter.check(client, policy);

        assertFalse(result.allowed());
        assertEquals(0, result.remaining());
    }
}