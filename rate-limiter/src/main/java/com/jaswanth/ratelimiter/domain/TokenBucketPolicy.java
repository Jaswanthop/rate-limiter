package com.jaswanth.ratelimiter.domain;

public record TokenBucketPolicy(int capacity, double refillRate) {
}
