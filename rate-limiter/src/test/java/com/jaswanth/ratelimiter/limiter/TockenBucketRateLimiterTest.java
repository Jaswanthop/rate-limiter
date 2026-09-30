package com.jaswanth.ratelimiter.limiter;

import com.jaswanth.ratelimiter.domain.ClientIdentity;
import com.jaswanth.ratelimiter.domain.RateLimitResult;
import com.jaswanth.ratelimiter.domain.TokenBucketPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenBucketRateLimiterTest {

    @Test
    void shouldAllowBurstUpToCapacity() {

        TokenBucketRateLimiter limiter =
                new TokenBucketRateLimiter();

        ClientIdentity client =
                new ClientIdentity(
                        "USER_ID",
                        "user-1"
                );

        TokenBucketPolicy policy =
                new TokenBucketPolicy(
                        5,
                        1
                );

        for (int i = 0; i < 5; i++) {

            RateLimitResult result =
                    limiter.check(client, policy);

            assertTrue(result.allowed());
        }

        RateLimitResult result =
                limiter.check(client, policy);

        assertFalse(result.allowed());
    }
}