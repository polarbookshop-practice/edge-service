package com.polarbookshop.edgeservice.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Configuration
public class RateLimiterConfig {
    @Bean
    public KeyResolver keyResolver() {
        // Configuring rate limiting for each user
        return exchange -> exchange.getPrincipal()
                .map(Principal::getName)
                // If the request is unauthenticated
                // it uses “anonymous” as the default key to apply rate-limiting
                .defaultIfEmpty("anonymous");
    }
}
