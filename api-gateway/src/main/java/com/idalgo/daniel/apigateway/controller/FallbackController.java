package com.idalgo.daniel.apigateway.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Fallback controller for circuit breaker.
 *
 * Provides graceful responses when backend services are unavailable.
 * Returns user-friendly error messages with proper HTTP status codes.
 */
@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @GetMapping("/orders")
    @PostMapping("/orders")
    @PutMapping("/orders/**")
    @DeleteMapping("/orders/**")
    public ResponseEntity<Map<String, Object>> orderServiceFallback() {
        log.warn("⚠️ Circuit Breaker: Order Service fallback triggered");

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "Order Service is currently unavailable. Please try again in a few moments.",
                        "service", "order-service",
                        "timestamp", LocalDateTime.now(),
                        "suggestion", "Check service status or contact support if the issue persists"
                ));
    }

    @GetMapping("/payments")
    @PostMapping("/payments")
    @PutMapping("/payments/**")
    @DeleteMapping("/payments/**")
    public ResponseEntity<Map<String, Object>> paymentServiceFallback() {
        log.warn("⚠️ Circuit Breaker: Payment Service fallback triggered");

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "Payment Service is currently unavailable. Your order has been saved and will be processed when the service is restored.",
                        "service", "payment-service",
                        "timestamp", LocalDateTime.now(),
                        "suggestion", "Please check back later or contact support"
                ));
    }

    @GetMapping("/notifications")
    @PostMapping("/notifications")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        log.warn("⚠️ Circuit Breaker: Notification Service fallback triggered");

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "Notification Service is temporarily unavailable. Notifications may be delayed.",
                        "service", "notification-service",
                        "timestamp", LocalDateTime.now()
                ));
    }
}