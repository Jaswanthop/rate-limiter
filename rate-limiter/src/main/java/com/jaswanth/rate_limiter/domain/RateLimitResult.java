package com.jaswanth.rate_limiter.domain;


public record RateLimitResult(
        boolean allowed,
        int limit,
        int remaining,
        long retryAfterSeconds
) {
}