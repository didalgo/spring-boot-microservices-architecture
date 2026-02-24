package com.idalgo.daniel.apigateway.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Fallback controller for circuit breaker.
 *
 * Provides fallback responses when backend services are unavailable.
 */
@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @GetMapping("/orders")
    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> orderServiceFallback() {
        log.warn("⚠️ Order Service fallback triggered");

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "Order Service is currently unavailable. Please try again later.",
                        "service", "order-service",
                        "timestamp", LocalDateTime.now()
                ));
    }

    @GetMapping("/payments")
    @PostMapping("/payments")
    public ResponseEntity<Map<String, Object>> paymentServiceFallback() {
        log.warn("⚠️ Payment Service fallback triggered");

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "Payment Service is currently unavailable. Please try again later.",
                        "service", "payment-service",
                        "timestamp", LocalDateTime.now()
                ));
    }
}