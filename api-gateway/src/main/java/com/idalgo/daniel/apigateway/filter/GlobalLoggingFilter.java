package com.idalgo.daniel.apigateway.filter;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Global filter that logs all incoming requests and outgoing responses.
 *
 * Executes for every request passing through the gateway.
 * Order: -1 (executes before other filters)
 */
@Component
@Slf4j
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Log request
        log.info("=".repeat(60));
        log.info("🌐 Incoming Request: {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI());
        log.info("Headers: {}", exchange.getRequest().getHeaders());

        long startTime = System.currentTimeMillis();

        // Process request and log response
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    long duration = System.currentTimeMillis() - startTime;

                    log.info("📤 Outgoing Response: {} ({}ms)",
                            exchange.getResponse().getStatusCode(),
                            duration);
                    log.info("=".repeat(60));
                }));
    }

    @Override
    public int getOrder() {
        return -1;  // High priority - executes first
    }
}