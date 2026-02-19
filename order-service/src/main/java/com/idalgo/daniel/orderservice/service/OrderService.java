package com.idalgo.daniel.orderservice.service;

import com.idalgo.daniel.orderservice.config.OrderServiceConfig;
import com.idalgo.daniel.orderservice.dto.CreateOrderRequest;
import com.idalgo.daniel.orderservice.dto.OrderResponse;
import com.idalgo.daniel.orderservice.exception.OrderNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service layer for order business logic.
 * 
 * Responsibilities:
 * - Order creation with ID generation
 * - Order retrieval and listing
 * - Order deletion
 * - Business rule enforcement
 * 
 * Current implementation uses in-memory storage (ConcurrentHashMap).
 * In future lessons, this will be replaced with database persistence.
 * 
 * @Service marks this as a Spring-managed bean
 * @RequiredArgsConstructor (Lombok) generates constructor for final fields
 * @Slf4j (Lombok) provides logging via 'log' field
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    // In-memory storage (temporary, will be replaced with database)
    private final Map<String, OrderResponse> orderStore = new ConcurrentHashMap<>();
    
    // Atomic counter for generating unique order IDs
    private final AtomicLong orderIdCounter = new AtomicLong(1);
    
    // Injected configuration
    private final OrderServiceConfig config;
    
    /**
     * Creates a new order.
     * 
     * Process:
     * 1. Generate unique order ID
     * 2. Create order with PENDING status
     * 3. Store in memory
     * 4. Return order response
     * 
     * @param request Order creation request
     * @return Created order with generated ID
     */
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.debug("Creating order for customer: {}", request.customerId());
        
        // Generate unique order ID using configured prefix
        String orderId = generateOrderId();
        
        // Create order response
        OrderResponse order = new OrderResponse(
            orderId,
            request.customerId(),
            request.productId(),
            request.quantity(),
            request.totalAmount(),
            "PENDING",
            LocalDateTime.now()
        );
        
        // Store order
        orderStore.put(orderId, order);
        
        log.info("Order created successfully: {}", orderId);
        
        // Log notification status (demonstrates using config)
        if (config.enableNotifications()) {
            log.debug("Notification would be sent for order: {}", orderId);
        }
        
        return order;
    }
    
    /**
     * Retrieves all orders.
     * 
     * @return List of all orders (unordered)
     */
    public List<OrderResponse> getAllOrders() {
        log.debug("Retrieving all orders. Current count: {}", orderStore.size());
        return List.copyOf(orderStore.values());
    }
    
    /**
     * Retrieves a specific order by ID.
     * 
     * @param orderId The order ID to retrieve
     * @return The order if found
     * @throws OrderNotFoundException if order doesn't exist
     */
    public OrderResponse getOrderById(String orderId) {
        log.debug("Retrieving order: {}", orderId);
        
        OrderResponse order = orderStore.get(orderId);
        
        if (order == null) {
            log.warn("Order not found: {}", orderId);
            throw new OrderNotFoundException(orderId);
        }
        
        return order;
    }
    
    /**
     * Deletes an order.
     * 
     * @param orderId The order ID to delete
     * @throws OrderNotFoundException if order doesn't exist
     */
    public void deleteOrder(String orderId) {
        log.debug("Deleting order: {}", orderId);
        
        OrderResponse removedOrder = orderStore.remove(orderId);
        
        if (removedOrder == null) {
            log.warn("Attempted to delete non-existent order: {}", orderId);
            throw new OrderNotFoundException(orderId);
        }
        
        log.info("Order deleted successfully: {}", orderId);
    }
    
    /**
     * Generates a unique order ID.
     * 
     * Format: {PREFIX}-{TIMESTAMP}-{COUNTER}
     * Example: ORD-1707825000-1
     * 
     * @return Generated order ID
     */
    private String generateOrderId() {
        long timestamp = System.currentTimeMillis() / 1000; // Unix timestamp in seconds
        long counter = orderIdCounter.getAndIncrement();
        
        return String.format("%s-%d-%d", 
            config.orderPrefix(), 
            timestamp, 
            counter
        );
    }
    
    /**
     * Gets current order count (useful for testing).
     * 
     * @return Number of orders in storage
     */
    public int getOrderCount() {
        return orderStore.size();
    }
}
