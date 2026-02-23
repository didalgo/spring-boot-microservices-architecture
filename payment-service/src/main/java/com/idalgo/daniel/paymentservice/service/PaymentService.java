package com.idalgo.daniel.paymentservice.service;

import com.idalgo.daniel.contracts.dto.payment.*;
import com.idalgo.daniel.paymentservice.config.PaymentServiceConfig;
import com.idalgo.daniel.paymentservice.exception.InvalidPaymentStateException;
import com.idalgo.daniel.paymentservice.exception.PaymentNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service layer for payment business logic.
 * 
 * Responsibilities:
 * - Payment processing with ID generation
 * - Payment status management
 * - Refund processing
 * - Business rule enforcement
 * 
 * Current implementation uses in-memory storage (ConcurrentHashMap).
 * In future lessons, this will be replaced with database persistence.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final Map<String, PaymentDTO> paymentStore = new ConcurrentHashMap<>();
    private final AtomicLong paymentIdCounter = new AtomicLong(1);
    private final PaymentServiceConfig config;
    
    /**
     * Processes a new payment.
     * 
     * Process:
     * 1. Generate unique payment ID
     * 2. Create payment with PENDING status
     * 3. Simulate payment processing
     * 4. Update status to COMPLETED or FAILED
     * 5. Return payment response
     * 
     * @param request Payment processing request
     * @return Processed payment
     */
    public PaymentDTO processPayment(ProcessPaymentRequest request) {
        log.debug("Processing payment for order: {}", request.orderId());
        
        String paymentId = generatePaymentId();
        LocalDateTime now = LocalDateTime.now();
        
        // Create initial payment with PENDING status
        PaymentDTO pendingPayment = new PaymentDTO(
            paymentId,
            request.orderId(),
            request.amount(),
            request.paymentMethod(),
            PaymentStatus.PENDING,
            request.customerEmail(),
            now,
            null  // Not processed yet
        );
        
        paymentStore.put(paymentId, pendingPayment);
        log.info("Payment created with ID: {}", paymentId);
        
        // Simulate payment processing
        boolean paymentSuccessful = simulatePaymentProcessing(request);
        
        // Update payment status
        PaymentStatus finalStatus = paymentSuccessful 
            ? PaymentStatus.COMPLETED 
            : PaymentStatus.FAILED;
        
        PaymentDTO processedPayment = new PaymentDTO(
            paymentId,
            request.orderId(),
            request.amount(),
            request.paymentMethod(),
            finalStatus,
            request.customerEmail(),
            now,
            LocalDateTime.now()  // Processed now
        );
        
        paymentStore.put(paymentId, processedPayment);
        
        log.info("Payment {} {}", paymentId, finalStatus);
        
        return processedPayment;
    }
    
    /**
     * Retrieves all payments.
     * 
     * @return List of all payments
     */
    public List<PaymentDTO> getAllPayments() {
        log.debug("Retrieving all payments. Current count: {}", paymentStore.size());
        return List.copyOf(paymentStore.values());
    }
    
    /**
     * Retrieves a specific payment by ID.
     * 
     * @param paymentId The payment ID to retrieve
     * @return The payment if found
     * @throws PaymentNotFoundException if payment doesn't exist
     */
    public PaymentDTO getPaymentById(String paymentId) {
        log.debug("Retrieving payment: {}", paymentId);
        
        PaymentDTO payment = paymentStore.get(paymentId);
        
        if (payment == null) {
            log.warn("Payment not found: {}", paymentId);
            throw new PaymentNotFoundException(paymentId);
        }
        
        return payment;
    }
    
    /**
     * Processes a refund for a payment.
     * 
     * Business rules:
     * - Payment must exist
     * - Payment must be in COMPLETED status
     * - Payment cannot already be REFUNDED
     * 
     * @param paymentId Payment to refund
     * @param request Refund request with reason
     * @return Refunded payment
     * @throws PaymentNotFoundException if payment doesn't exist
     * @throws InvalidPaymentStateException if payment cannot be refunded
     */
    public PaymentDTO refundPayment(String paymentId, RefundRequest request) {
        log.debug("Processing refund for payment: {}", paymentId);
        
        PaymentDTO existingPayment = getPaymentById(paymentId);
        
        // Validate payment can be refunded
        if (existingPayment.status() != PaymentStatus.COMPLETED) {
            log.warn("Cannot refund payment {} in status {}", 
                paymentId, existingPayment.status());
            throw new InvalidPaymentStateException(
                paymentId,
                existingPayment.status(),
                "refund"
            );
        }
        
        // Process refund
        PaymentDTO refundedPayment = new PaymentDTO(
            existingPayment.paymentId(),
            existingPayment.orderId(),
            existingPayment.amount(),
            existingPayment.paymentMethod(),
            PaymentStatus.REFUNDED,
            existingPayment.customerEmail(),
            existingPayment.createdAt(),
            LocalDateTime.now()  // Update processed time
        );
        
        paymentStore.put(paymentId, refundedPayment);
        
        log.info("Payment {} refunded. Reason: {}", paymentId, request.reason());
        
        return refundedPayment;
    }
    
    /**
     * Simulates payment processing.
     * 
     * In a real system, this would:
     * - Call payment gateway API
     * - Validate card details
     * - Process transaction
     * - Handle 3D Secure
     * 
     * For now, we simulate:
     * - 90% success rate for card payments
     * - 100% success for other methods
     * 
     * @param request Payment request
     * @return true if payment successful, false otherwise
     */
    private boolean simulatePaymentProcessing(ProcessPaymentRequest request) {
        // Simulate processing delay
        try {
            Thread.sleep(100);  // 100ms processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simple simulation logic
        if (request.paymentMethod() == PaymentMethod.CREDIT_CARD ||
            request.paymentMethod() == PaymentMethod.DEBIT_CARD) {
            // 90% success rate for cards
            return Math.random() > 0.1;
        }
        
        // 100% success for other methods (simplified)
        return true;
    }
    
    /**
     * Generates a unique payment ID.
     * 
     * Format: {PREFIX}-{TIMESTAMP}-{COUNTER}
     * Example: PAY-1707825000-1
     * 
     * @return Generated payment ID
     */
    private String generatePaymentId() {
        long timestamp = System.currentTimeMillis() / 1000;
        long counter = paymentIdCounter.getAndIncrement();
        
        return String.format("%s-%d-%d", 
            config.paymentPrefix(), 
            timestamp, 
            counter
        );
    }
    
    /**
     * Gets current payment count (useful for testing).
     * 
     * @return Number of payments in storage
     */
    public int getPaymentCount() {
        return paymentStore.size();
    }
}
