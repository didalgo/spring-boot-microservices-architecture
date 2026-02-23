package com.idalgo.daniel.paymentservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Type-safe configuration properties for Payment Service.
 * 
 * Properties are bound from application.yml with prefix "payment-service".
 * 
 * @param paymentPrefix Prefix for generated payment IDs
 * @param enableFraudDetection Whether to enable fraud detection (future feature)
 * @param maxRefundDays Maximum days after payment to allow refunds
 */
@ConfigurationProperties(prefix = "payment-service")
@Validated
public record PaymentServiceConfig(
    
    @NotBlank(message = "Payment prefix cannot be blank")
    String paymentPrefix,
    
    boolean enableFraudDetection,
    
    @Min(value = 1, message = "Max refund days must be at least 1")
    @Max(value = 365, message = "Max refund days cannot exceed 365")
    int maxRefundDays
) {
    
    public PaymentServiceConfig {
        if (paymentPrefix != null && paymentPrefix.length() > 10) {
            throw new IllegalArgumentException("Payment prefix too long (max 10 chars)");
        }
    }
}
