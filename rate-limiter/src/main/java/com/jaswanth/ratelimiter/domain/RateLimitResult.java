package com.jaswanth.ratelimiter.domain;


public record RateLimitResult(
        boolean allowed,
        int limit,
        int remaining,
        long retryAfterSeconds
) {
}