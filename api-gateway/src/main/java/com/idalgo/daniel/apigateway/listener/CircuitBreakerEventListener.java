package com.idalgo.daniel.apigateway.listener;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.event.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener for Circuit Breaker state transition events.
 *
 * Logs important events like:
 * - State changes (CLOSED → OPEN → HALF_OPEN)
 * - Circuit breaker triggers
 * - Error rate changes
 */
@Component
@Slf4j
public class CircuitBreakerEventListener {

    @EventListener
    public void onCircuitBreakerStateTransition(CircuitBreakerOnStateTransitionEvent event) {
        log.warn("🔴 Circuit Breaker '{}' transitioned from {} to {}",
                event.getCircuitBreakerName(),
                event.getStateTransition().getFromState(),
                event.getStateTransition().getToState()
        );

        // Alert or metrics can be sent here
        if (event.getStateTransition().getToState() == CircuitBreaker.State.OPEN) {
            log.error("⚠️ ALERT: Circuit Breaker '{}' is now OPEN! Service is degraded.",
                    event.getCircuitBreakerName());
        }

        if (event.getStateTransition().getToState() == CircuitBreaker.State.CLOSED) {
            log.info("✅ Circuit Breaker '{}' is now CLOSED. Service recovered.",
                    event.getCircuitBreakerName());
        }
    }

    @EventListener
    public void onCircuitBreakerError(CircuitBreakerOnErrorEvent event) {
        log.debug("❌ Circuit Breaker '{}' recorded error: {}",
                event.getCircuitBreakerName(),
                event.getThrowable().getMessage()
        );
    }

    @EventListener
    public void onCircuitBreakerSuccess(CircuitBreakerOnSuccessEvent event) {
        log.trace("✅ Circuit Breaker '{}' recorded success (duration: {}ms)",
                event.getCircuitBreakerName(),
                event.getElapsedDuration().toMillis()
        );
    }

    @EventListener
    public void onCircuitBreakerSlowCall(CircuitBreakerOnSlowCallRateExceededEvent event) {
        log.warn("⏱️ Circuit Breaker '{}' detected slow calls: {}%",
                event.getCircuitBreakerName(),
                event.getSlowCallRate()
        );
    }

    @EventListener
    public void onCircuitBreakerFailureRate(CircuitBreakerOnFailureRateExceededEvent event) {
        log.warn("📊 Circuit Breaker '{}' exceeded failure rate: {}%",
                event.getCircuitBreakerName(),
                event.getFailureRate()
        );
    }
}