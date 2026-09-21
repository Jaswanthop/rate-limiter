package com.jaswanth.rate_limiter.limiter;


import com.jaswanth.rate_limiter.domain.ClientIdentity;
import com.jaswanth.rate_limiter.domain.RateLimitPolicy;
import com.jaswanth.rate_limiter.domain.RateLimitResult;

public interface RateLimiter {

    RateLimitResult check(
            ClientIdentity client,
            RateLimitPolicy policy
    );
}