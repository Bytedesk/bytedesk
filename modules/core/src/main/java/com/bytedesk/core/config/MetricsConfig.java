package com.bytedesk.starter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter onlineUsersCounter(MeterRegistry registry) {
        return Counter.builder("bytedesk.online.users")
                .description("Number of online users")
                .register(registry);
    }

    @Bean
    public Counter messageCounter(MeterRegistry registry) {
        return Counter.builder("bytedesk.messages")
                .description("Number of messages processed")
                .register(registry);
    }

    @Bean
    public Counter aiRequestCounter(MeterRegistry registry) {
        return Counter.builder("bytedesk.ai.requests")
                .description("Number of AI requests processed")
                .register(registry);
    }
} 