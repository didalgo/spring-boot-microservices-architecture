package com.idalgo.daniel.paymentservice.controller;

import com.idalgo.daniel.contracts.dto.payment.*;
import com.idalgo.daniel.paymentservice.service.PaymentService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Payment Management.
 * 
 * Base path: /api/payments
 * 
 * Endpoints:
 * - POST   /api/payments             - Process payment (201 Created)
 * - GET    /api/payments             - List all payments (200 OK)
 * - GET    /api/payments/{id}        - Get specific payment (200 OK)
 * - PUT    /api/payments/{id}/refund - Refund payment (200 OK)
 * 
 * Error handling:
 * - All exceptions are handled by GlobalExceptionHandler from common module
 * - Returns consistent ErrorResponse format
 * - Proper HTTP status codes (404, 400, etc.)
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * Processes a new payment.
     * 
     * HTTP POST /api/payments
     * 
     * Request body: ProcessPaymentRequest (JSON)
     * Response: PaymentDTO with 201 Created
     * 
     * @param request Payment processing request
     * @return Processed payment with HTTP 201
     */
    @PostMapping
    public ResponseEntity<PaymentDTO> processPayment(
        @Valid @RequestBody ProcessPaymentRequest request
    ) {
        log.info("Received request to process payment for order: {}", request.orderId());
        
        PaymentDTO processedPayment = paymentService.processPayment(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(processedPayment);
    }
    
    /**
     * Retrieves all payments.
     * 
     * HTTP GET /api/payments
     * 
     * Response: List of PaymentDTO with 200 OK
     * 
     * @return List of all payments
     */
    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        log.info("Received request to list all payments");
        
        List<PaymentDTO> payments = paymentService.getAllPayments();
        
        log.debug("Returning {} payments", payments.size());
        
        return ResponseEntity.ok(payments);
    }
    
    /**
     * Retrieves a specific payment by ID.
     * 
     * HTTP GET /api/payments/{paymentId}
     * 
     * Path variable: paymentId
     * Response: PaymentDTO with 200 OK
     * 
     * @param paymentId Payment ID to retrieve
     * @return Payment details
     * @throws PaymentNotFoundException if payment not found (handled by GlobalExceptionHandler → 404)
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable String paymentId) {
        log.info("Received request to get payment: {}", paymentId);
        
        PaymentDTO payment = paymentService.getPaymentById(paymentId);
        
        return ResponseEntity.ok(payment);
    }
    
    /**
     * Processes a refund for a payment.
     * 
     * HTTP PUT /api/payments/{paymentId}/refund
     * 
     * Path variable: paymentId
     * Request body: RefundRequest (JSON)
     * Response: PaymentDTO with 200 OK
     * 
     * Business rules enforced:
     * - Payment must exist (404 if not)
     * - Payment must be COMPLETED (400 if not)
     * 
     * @param paymentId Payment ID to refund
     * @param request Refund request with reason
     * @return Refunded payment
     * @throws PaymentNotFoundException if payment not found
     * @throws InvalidPaymentStateException if payment cannot be refunded
     */
    @PutMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentDTO> refundPayment(
        @PathVariable String paymentId,
        @Valid @RequestBody RefundRequest request
    ) {
        log.info("Received request to refund payment: {}", paymentId);
        
        PaymentDTO refundedPayment = paymentService.refundPayment(paymentId, request);
        
        return ResponseEntity.ok(refundedPayment);
    }
}
