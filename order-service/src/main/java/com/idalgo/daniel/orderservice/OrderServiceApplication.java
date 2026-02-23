package com.idalgo.daniel.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Main application class for Order Service.
 * 
 * This microservice handles order management operations including:
 * - Creating new orders
 * - Retrieving order information
 * - Listing all orders
 * - Deleting orders
 * 
 * Updated in Lesson 3:
 * - Now scans com.idalgo.daniel.common package to pick up GlobalExceptionHandler
 * - Uses shared error handling from common module
 * 
 * @SpringBootApplication enables:
 * - @Configuration: Marks this as a configuration class
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration
 * - @ComponentScan: Scans for components in specified packages
 * 
 * @ConfigurationPropertiesScan enables scanning for @ConfigurationProperties classes
 */
@SpringBootApplication(scanBasePackages = {
    "com.idalgo.daniel.orderservice",
    "com.idalgo.daniel.common"  // Include common module for GlobalExceptionHandler
})
@ConfigurationPropertiesScan
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
