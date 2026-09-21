package com.jaswanth.rate_limiter.domain;

public record RateLimitPolicy(
        int limit,
        long windowSeconds
) {
}