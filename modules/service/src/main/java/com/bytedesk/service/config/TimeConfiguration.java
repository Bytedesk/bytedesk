package com.bytedesk.service.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfiguration {

    @Bean
    public Clock systemClock() {
        // Provide a centralized clock so services can be unit-tested with overrides later.
        return Clock.systemUTC();
    }
}
