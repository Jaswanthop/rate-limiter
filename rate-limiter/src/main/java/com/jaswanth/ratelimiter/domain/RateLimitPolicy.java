package com.jaswanth.ratelimiter.domain;

public record RateLimitPolicy(
        int limit,
        long windowSeconds
) {
}