package com.idalgo.daniel.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway Application.
 *
 * Single entry point for all microservices in the system.
 *
 * Features:
 * - Routes requests to appropriate backend services
 * - Global CORS configuration
 * - Request/response logging
 * - Circuit breaker integration
 * - Fallback endpoints for errors
 *
 * Port: 8080
 *
 * Routes:
 * - /api/orders/** → order-service (8081)
 * - /api/payments/** → payment-service (8082)
 * - /api/notifications/** → notification-service (8083)
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}