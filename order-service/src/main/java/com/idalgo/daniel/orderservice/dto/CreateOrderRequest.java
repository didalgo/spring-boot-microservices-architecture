package com.idalgo.daniel.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new order.
 * 
 * Uses Java 21 Record for:
 * - Immutability (all fields are final)
 * - Automatic getters, equals, hashCode, toString
 * - Concise syntax
 * 
 * Bean Validation annotations ensure data integrity:
 * - @NotBlank: String cannot be null, empty, or whitespace-only
 * - @NotNull: Field cannot be null
 * - @Positive: Number must be > 0
 * 
 * Jackson (JSON library) automatically serializes/deserializes Records
 * without additional configuration.
 * 
 * Example JSON:
 * {
 *   "customerId": "CUST-12345",
 *   "productId": "PROD-67890",
 *   "quantity": 2,
 *   "totalAmount": 199.98
 * }
 * 
 * @param customerId Unique identifier of the customer placing the order
 * @param productId Unique identifier of the product being ordered
 * @param quantity Number of units ordered
 * @param totalAmount Total cost of the order
 */
public record CreateOrderRequest(
    
    @NotBlank(message = "Customer ID is required")
    String customerId,
    
    @NotBlank(message = "Product ID is required")
    String productId,
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    Integer quantity,
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be greater than 0")
    BigDecimal totalAmount
) {
}
