package com.idalgo.daniel.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Main application class for Payment Service.
 * 
 * This microservice handles payment processing operations including:
 * - Processing new payments
 * - Retrieving payment information
 * - Listing all payments
 * - Processing refunds
 * 
 * Payment methods supported:
 * - Credit Card
 * - Debit Card
 * - PayPal
 * - Bank Transfer
 * 
 * @SpringBootApplication enables:
 * - @Configuration: Marks this as a configuration class
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration
 * - @ComponentScan: Scans for components in this package and sub-packages
 * 
 * @ConfigurationPropertiesScan enables scanning for @ConfigurationProperties classes
 * 
 * Note: This service also scans com.idalgo.daniel.common package to pick up
 * GlobalExceptionHandler from the common module.
 */
@SpringBootApplication(scanBasePackages = {
    "com.idalgo.daniel.paymentservice",
    "com.idalgo.daniel.common"  // Include common module for GlobalExceptionHandler
})
@ConfigurationPropertiesScan
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
