package com.idalgo.daniel.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for order information.
 * 
 * This DTO is returned when:
 * - Creating a new order (201 Created)
 * - Retrieving an existing order (200 OK)
 * - Listing orders (200 OK with array)
 * 
 * Uses Java 21 Record for immutability and automatic methods.
 * 
 * Jackson annotations customize JSON serialization:
 * - @JsonFormat: Controls date/time formatting
 * 
 * Example JSON response:
 * {
 *   "orderId": "ORD-1234567890",
 *   "customerId": "CUST-12345",
 *   "productId": "PROD-67890",
 *   "quantity": 2,
 *   "totalAmount": 199.98,
 *   "status": "PENDING",
 *   "createdAt": "2024-02-13T10:30:00"
 * }
 * 
 * @param orderId Unique identifier of the order
 * @param customerId Customer who placed the order
 * @param productId Product that was ordered
 * @param quantity Number of units
 * @param totalAmount Total cost
 * @param status Current status of the order
 * @param createdAt Timestamp when order was created
 */
public record OrderResponse(
    
    String orderId,
    String customerId,
    String productId,
    Integer quantity,
    BigDecimal totalAmount,
    String status,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt
) {
}
