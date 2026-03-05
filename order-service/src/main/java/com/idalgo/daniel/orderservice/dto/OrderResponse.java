package com.idalgo.daniel.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.idalgo.daniel.contracts.dto.order.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

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
 * @param paymentId if payment was processed
 * @param createdAt Timestamp when order was created
 */
@Schema(description = "Order response with complete order details")
public record OrderResponse(

    @Schema(
            description = "Unique order identifier",
            example = "ORD-1677849600-1"
    )        
    String orderId,

    @Schema(
            description = "Customer unique identifier",
            example = "CUST-001"
    )
    String customerId,

    @Schema(
            description = "Product unique identifier",
            example = "PROD-001"
    )
    String productId,

    @Schema(
            description = "Quantity of products ordered",
            example = "2",
            minimum = "1"
    )
    Integer quantity,

    @Schema(
            description = "Total amount in USD",
            example = "199.99"
    )
    BigDecimal totalAmount,

    @Schema(
            description = "Current order status",
            example = "CONFIRMED",
            allowableValues = {"PENDING", "CONFIRMED", "PAYMENT_FAILED", "CANCELLED"}
    )
    OrderStatus status,
    
    @Schema(
            description = "Payment transaction unique identifier returned by payment service",
            example = "PAY-1677849601-1",
            nullable = true
    )
    String paymentId,

    @Schema(
            description = "Order creation timestamp",
            example = "2024-03-05T10:30:00",
            type = "string",
            format = "date-time"
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt
) {
}
