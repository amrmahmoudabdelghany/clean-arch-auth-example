package com.gray.auth.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PerUserRateLimiterService {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final Map<String, RateLimiter> userRateLimiters = new ConcurrentHashMap<>();

    public PerUserRateLimiterService() {
        // Initialize a default rate limiter configuration with an 8-call limit per hour
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(8)
                .limitRefreshPeriod(Duration.ofHours(1))
                .timeoutDuration(Duration.ZERO)
                .build();
        this.rateLimiterRegistry = RateLimiterRegistry.of(config);
    }

    public boolean isAllowed(String userIdentifier) {
        // Get or create a RateLimiter instance for this user identifier
        RateLimiter rateLimiter = userRateLimiters.computeIfAbsent(userIdentifier,
                id -> rateLimiterRegistry.rateLimiter(id));
        // Check if a call is permitted
        return rateLimiter.acquirePermission();
    }
}

