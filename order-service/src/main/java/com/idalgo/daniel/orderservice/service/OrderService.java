package com.idalgo.daniel.orderservice.service;

import com.idalgo.daniel.orderservice.config.OrderServiceConfig;
import com.idalgo.daniel.orderservice.dto.CreateOrderRequest;
import com.idalgo.daniel.orderservice.dto.OrderResponse;
import com.idalgo.daniel.orderservice.exception.OrderNotFoundException;
import com.idalgo.daniel.contracts.dto.order.OrderStatus;
import com.idalgo.daniel.contracts.dto.payment.PaymentDTO;
import com.idalgo.daniel.contracts.dto.payment.PaymentMethod;
import com.idalgo.daniel.contracts.dto.payment.PaymentStatus;
import com.idalgo.daniel.contracts.dto.payment.ProcessPaymentRequest;
import com.idalgo.daniel.orderservice.client.PaymentClient;
import com.idalgo.daniel.orderservice.client.PaymentServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import com.idalgo.daniel.contracts.events.OrderConfirmedEvent;
import org.springframework.kafka.core.KafkaTemplate;

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
    
    // Payment client
    private final PaymentClient paymentClient;

    private final KafkaTemplate<String, OrderConfirmedEvent> kafkaTemplate; 
    
    /**
     * Creates a new order.
     * 
     * Process:
     * 1. Generate unique order ID
     * 2. Create order with PENDING status
     * 3. Call Payment Service
     * 4. Update status based on payment result
     * 5. Store in memory
     * 6. Return order response
     * 
     * @param request Order creation request
     * @return Created order with generated ID
     */
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.debug("Creating order for customer: {}", request.customerId());

        String orderId = generateOrderId();

        // Create initial order with PENDING status
        OrderResponse pendingOrder = new OrderResponse(
                orderId,
                request.customerId(),
                request.productId(),
                request.quantity(),
                request.totalAmount(),
                OrderStatus.PENDING,    // NUEVO
                null,                   // No payment ID yet
                LocalDateTime.now()
        );

        orderStore.put(orderId, pendingOrder);
        log.info("Order created with ID: {} in PENDING status", orderId);

        // Process payment
        try {
            // Update to PAYMENT_PROCESSING
            updateOrderStatus(orderId, OrderStatus.PAYMENT_PROCESSING);

            // Call Payment Service
            ProcessPaymentRequest paymentRequest = new ProcessPaymentRequest(
                    orderId,
                    request.totalAmount(),
                    PaymentMethod.CREDIT_CARD,  // Simplificado por ahora
                    request.customerId() + "@example.com",  // Email simulado
                    "4111111111111111"  // Card simulado
            );

            PaymentDTO payment = paymentClient.processPayment(paymentRequest);

            // Update order based on payment result
            OrderStatus finalStatus = payment.status() == PaymentStatus.COMPLETED
                    ? OrderStatus.CONFIRMED
                    : OrderStatus.PAYMENT_FAILED;

            OrderResponse finalOrder = new OrderResponse(
                    orderId,
                    request.customerId(),
                    request.productId(),
                    request.quantity(),
                    request.totalAmount(),
                    finalStatus,
                    payment.paymentId(),
                    pendingOrder.createdAt()
            );

            orderStore.put(orderId, finalOrder);
            log.info("Order {} finalized with status: {}", orderId, finalStatus);

            if (finalStatus == OrderStatus.CONFIRMED) {
                publishOrderConfirmedEvent(finalOrder);
            }
            
            return finalOrder;

        } catch (PaymentServiceException e) {
            log.error("Payment service failed for order: {}", orderId, e);

            // Update order to PAYMENT_FAILED
            OrderResponse failedOrder = new OrderResponse(
                    orderId,
                    request.customerId(),
                    request.productId(),
                    request.quantity(),
                    request.totalAmount(),
                    OrderStatus.PAYMENT_FAILED,
                    null,
                    pendingOrder.createdAt()
            );

            orderStore.put(orderId, failedOrder);

            return failedOrder;
        }
    }

    // Helper method for status update
    private void updateOrderStatus(String orderId, OrderStatus newStatus) {
        OrderResponse current = orderStore.get(orderId);
        if (current != null) {
            OrderResponse updated = new OrderResponse(
                    current.orderId(),
                    current.customerId(),
                    current.productId(),
                    current.quantity(),
                    current.totalAmount(),
                    newStatus,
                    current.paymentId(),
                    current.createdAt()
            );
            orderStore.put(orderId, updated);
            log.debug("Order {} status updated to {}", orderId, newStatus);
        }
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

    /**
     * Publishes OrderConfirmedEvent to Kafka.
     */
    private void publishOrderConfirmedEvent(OrderResponse order) {
        OrderConfirmedEvent event = OrderConfirmedEvent.create(
                order.orderId(),
                order.customerId(),
                order.totalAmount(),
                order.paymentId()
        );

        log.info("Publishing OrderConfirmedEvent for order: {}", order.orderId());

        kafkaTemplate.send("order-events", order.orderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Event published successfully: {}", event.eventId());
                    } else {
                        log.error("Failed to publish event", ex);
                    }
                });
    } 
}
