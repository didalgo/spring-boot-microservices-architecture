package com.idalgo.daniel.orderservice.controller;

import com.idalgo.daniel.orderservice.config.DynamicConfig;
import com.idalgo.daniel.orderservice.dto.CreateOrderRequest;
import com.idalgo.daniel.orderservice.dto.OrderResponse;
import com.idalgo.daniel.orderservice.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Order Management.
 * 
 * Provides HTTP endpoints for CRUD operations on orders.
 * 
 * Base path: /api/orders
 * 
 * Endpoints:
 * - POST   /api/orders          - Create new order (201 Created)
 * - GET    /api/orders          - List all orders (200 OK)
 * - GET    /api/orders/{id}     - Get specific order (200 OK)
 * - DELETE /api/orders/{id}     - Delete order (204 No Content)
 * 
 * Best practices implemented:
 * - Proper HTTP verbs for operations
 * - Appropriate status codes
 * - Request validation with @Valid
 * - Logging of operations
 * - RESTful resource naming
 * 
 * @RestController combines @Controller + @ResponseBody
 * @RequestMapping defines base path for all endpoints
 * @RequiredArgsConstructor (Lombok) injects dependencies
 * @Slf4j (Lombok) provides logging
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Order management operations")
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * Creates a new order.
     * 
     * HTTP POST /api/orders
     * 
     * Request body: CreateOrderRequest (JSON)
     * Response: OrderResponse with 201 Created
     * 
     * @Valid triggers Bean Validation on the request body.
     * If validation fails, Spring returns 400 Bad Request automatically.
     * 
     * @param request Order creation request
     * @return Created order with HTTP 201
     */
    @Operation(
            summary = "Create a new order",
            description = "Creates a new order and processes payment. " +
                    "The order will be in PENDING status initially, then CONFIRMED if payment succeeds."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error or payment processing failed"
            )
    })    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";

        log.info("User {} creating order for customer: {}", username, request.customerId());
        
        OrderResponse createdOrder = orderService.createOrder(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdOrder);
    }
    
    /**
     * Retrieves all orders.
     * 
     * HTTP GET /api/orders
     * 
     * Response: List of OrderResponse with 200 OK
     * 
     * Note: In production, this should be paginated.
     * We'll add pagination in future lessons.
     * 
     * @return List of all orders
     */
    @Operation(
            summary = "Get all orders (Admin only)",
            description = "Retrieves all orders in the system. Admin access required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of orders retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("Admin retrieving all orders");
        List<OrderResponse> orders = orderService.getAllOrders();
        log.debug("Returning {} orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves my orders.
     *
     * HTTP GET /api/my-orders
     *
     * Response: List of OrderResponse with 200 OK
     *
     * @return List of my orders
     */
    @Operation(
            summary = "Get my orders",
            description = "Retrieves orders for the authenticated user"
    )
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        log.info("User {} retrieving their orders", username);

        // TODO: Filter orders by username
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }   
    
    /**
     * Retrieves a specific order by ID.
     * 
     * HTTP GET /api/orders/{orderId}
     * 
     * Path variable: orderId
     * Response: OrderResponse with 200 OK
     * 
     * If order not found, OrderNotFoundException is thrown,
     * which will result in 500 (for now). In Lesson 3, we'll
     * add proper exception handling to return 404.
     * 
     * @param orderId Order ID to retrieve
     * @return Order details
     */
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves a specific order by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(
                    description = "Order ID in format ORD-{timestamp}-{sequence}",
                    example = "ORD-1677849600-1",
                    required = true
            )
            @PathVariable String orderId) {
        log.info("Received request to get order: {}", orderId);
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }
    
    /**
     * Deletes an order.
     * 
     * HTTP DELETE /api/orders/{orderId}
     * 
     * Path variable: orderId
     * Response: 204 No Content (no body)
     * 
     * DELETE is idempotent: deleting the same order multiple times
     * should have the same effect as deleting it once.
     * However, our current implementation throws exception on second delete.
     * This is acceptable for now.
     * 
     * @param orderId Order ID to delete
     * @return Empty response with 204
     */
    @Operation(
            summary = "Delete an order (Admin only)",
            description = "Deletes an order. Admin access required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Order deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(
                    description = "Order ID to delete",
                    example = "ORD-1677849600-1",
                    required = true
            )
            @PathVariable String orderId) {
        log.info("Admin deleting order: {}", orderId);
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

}
