package com.xin.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    @SuppressWarnings("UnstableApiUsage")
    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(5); // 可以调整为你需要的速率
    }
}