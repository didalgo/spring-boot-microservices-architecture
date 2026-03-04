package com.idalgo.daniel.notificationservice.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

/**
 * Configuration to enable context propagation for Kafka consumers.
 * Required for tracing to work with asynchronous Kafka message processing.
 */
@Configuration
public class ObservabilityConfig {

    @PostConstruct
    public void enableAutomaticContextPropagation() {
        Hooks.enableAutomaticContextPropagation();
    }
}