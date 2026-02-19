package com.idalgo.daniel.orderservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Type-safe configuration properties for Order Service.
 * 
 * Uses Java 21 Record for immutability and conciseness.
 * Properties are bound from application.yml with prefix "order-service".
 * 
 * Benefits of using @ConfigurationProperties:
 * - Type-safe: Compilation errors if misconfigured
 * - Validation: Bean Validation constraints enforced
 * - IDE support: Auto-completion in application.yml
 * - Documentation: Metadata generated for properties
 * - Testability: Easy to mock/override in tests
 * 
 * @param maxOrdersPerCustomer Maximum orders a single customer can have
 * @param orderPrefix Prefix for generated order IDs
 * @param enableNotifications Whether to enable order notifications
 */
@ConfigurationProperties(prefix = "order-service")
@Validated
public record OrderServiceConfig(
    
    @Min(value = 1, message = "Max orders per customer must be at least 1")
    @Max(value = 1000, message = "Max orders per customer cannot exceed 1000")
    int maxOrdersPerCustomer,
    
    @NotBlank(message = "Order prefix cannot be blank")
    String orderPrefix,
    
    boolean enableNotifications
) {
    
    /**
     * Compact constructor for additional validation if needed.
     * 
     * Records automatically generate a canonical constructor,
     * but we can add validation logic here.
     */
    public OrderServiceConfig {
        // Additional validation could go here
        // For example, checking orderPrefix format
        if (orderPrefix != null && orderPrefix.length() > 10) {
            throw new IllegalArgumentException("Order prefix too long (max 10 chars)");
        }
    }
}
