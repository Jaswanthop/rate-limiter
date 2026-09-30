package com.jaswanth.ratelimiter.limiter;


import com.jaswanth.ratelimiter.domain.ClientIdentity;
import com.jaswanth.ratelimiter.domain.RateLimitPolicy;
import com.jaswanth.ratelimiter.domain.RateLimitResult;

public interface RateLimiter {

    RateLimitResult check(
            ClientIdentity client,
            RateLimitPolicy policy
    );
}