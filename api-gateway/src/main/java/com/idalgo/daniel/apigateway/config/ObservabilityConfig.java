package com.idalgo.daniel.apigateway.config;

import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ContextSnapshotFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

/**
 * Configuration to enable context propagation in reactive (WebFlux) Gateway.
 * Required for tracing to work with Spring Cloud Gateway.
 */
@Configuration
public class ObservabilityConfig {

    @PostConstruct
    public void enableAutomaticContextPropagation() {
        Hooks.enableAutomaticContextPropagation();
    }
}