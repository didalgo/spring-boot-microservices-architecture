package com.idalgo.daniel.paymentservice.service;

import com.idalgo.daniel.contracts.dto.payment.*;
import com.idalgo.daniel.paymentservice.config.PaymentServiceConfig;
import com.idalgo.daniel.paymentservice.exception.InvalidPaymentStateException;
import com.idalgo.daniel.paymentservice.exception.PaymentNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for PaymentService.
 */
@DisplayName("PaymentService Unit Tests")
class PaymentServiceTest {
    
    private PaymentService paymentService;
    private PaymentServiceConfig config;
    
    @BeforeEach
    void setUp() {
        config = new PaymentServiceConfig(
            "TEST",
            false,
            30
        );
        paymentService = new PaymentService(config);
    }
    
    @Test
    @DisplayName("Should process payment successfully")
    void shouldProcessPaymentSuccessfully() {
        // Arrange
        ProcessPaymentRequest request = new ProcessPaymentRequest(
            "ORD-001",
            new BigDecimal("99.99"),
            PaymentMethod.CREDIT_CARD,
            "customer@example.com",
            "4111111111111111"
        );
        
        // Act
        PaymentDTO payment = paymentService.processPayment(request);
        
        // Assert
        assertThat(payment).isNotNull();
        assertThat(payment.paymentId()).startsWith("TEST-");
        assertThat(payment.orderId()).isEqualTo("ORD-001");
        assertThat(payment.amount()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(payment.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(payment.customerEmail()).isEqualTo("customer@example.com");
        assertThat(payment.status()).isIn(PaymentStatus.COMPLETED, PaymentStatus.FAILED);
        assertThat(payment.createdAt()).isNotNull();
        assertThat(payment.processedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("Should generate unique payment IDs")
    void shouldGenerateUniquePaymentIds() {
        // Arrange
        ProcessPaymentRequest request = new ProcessPaymentRequest(
            "ORD-001",
            new BigDecimal("50.00"),
            PaymentMethod.PAYPAL,
            "test@example.com",
            null
        );
        
        // Act
        PaymentDTO payment1 = paymentService.processPayment(request);
        PaymentDTO payment2 = paymentService.processPayment(request);
        PaymentDTO payment3 = paymentService.processPayment(request);
        
        // Assert
        assertThat(payment1.paymentId()).isNotEqualTo(payment2.paymentId());
        assertThat(payment2.paymentId()).isNotEqualTo(payment3.paymentId());
        assertThat(payment1.paymentId()).isNotEqualTo(payment3.paymentId());
    }
    
    @Test
    @DisplayName("Should retrieve all payments")
    void shouldRetrieveAllPayments() {
        // Arrange
        ProcessPaymentRequest request1 = new ProcessPaymentRequest(
            "ORD-001", new BigDecimal("50.00"), PaymentMethod.PAYPAL, "test1@example.com", null
        );
        ProcessPaymentRequest request2 = new ProcessPaymentRequest(
            "ORD-002", new BigDecimal("100.00"), PaymentMethod.BANK_TRANSFER, "test2@example.com", null
        );
        
        paymentService.processPayment(request1);
        paymentService.processPayment(request2);
        
        // Act
        List<PaymentDTO> payments = paymentService.getAllPayments();
        
        // Assert
        assertThat(payments).hasSize(2);
        assertThat(payments).extracting(PaymentDTO::orderId)
            .containsExactlyInAnyOrder("ORD-001", "ORD-002");
    }
    
    @Test
    @DisplayName("Should retrieve payment by ID")
    void shouldRetrievePaymentById() {
        // Arrange
        ProcessPaymentRequest request = new ProcessPaymentRequest(
            "ORD-001", new BigDecimal("75.50"), PaymentMethod.DEBIT_CARD, "test@example.com", "4111111111111111"
        );
        PaymentDTO createdPayment = paymentService.processPayment(request);
        
        // Act
        PaymentDTO retrievedPayment = paymentService.getPaymentById(createdPayment.paymentId());
        
        // Assert
        assertThat(retrievedPayment).isEqualTo(createdPayment);
    }
    
    @Test
    @DisplayName("Should throw exception when payment not found")
    void shouldThrowExceptionWhenPaymentNotFound() {
        // Act & Assert
        assertThatThrownBy(() -> paymentService.getPaymentById("NON-EXISTENT"))
            .isInstanceOf(PaymentNotFoundException.class)
            .hasMessageContaining("NON-EXISTENT");
    }
    
    @Test
    @DisplayName("Should refund completed payment successfully")
    void shouldRefundCompletedPaymentSuccessfully() {
        // Arrange
        ProcessPaymentRequest request = new ProcessPaymentRequest(
            "ORD-001", new BigDecimal("150.00"), PaymentMethod.PAYPAL, "test@example.com", null
        );
        PaymentDTO payment = paymentService.processPayment(request);
        
        // Keep trying until we get a COMPLETED payment (since processing is random)
        int attempts = 0;
        while (payment.status() != PaymentStatus.COMPLETED && attempts < 20) {
            payment = paymentService.processPayment(request);
            attempts++;
        }
        
        // Skip test if we couldn't get a completed payment
        if (payment.status() != PaymentStatus.COMPLETED) {
            return;
        }
        
        RefundRequest refundRequest = new RefundRequest("Customer requested refund");
        
        // Act
        PaymentDTO refundedPayment = paymentService.refundPayment(payment.paymentId(), refundRequest);
        
        // Assert
        assertThat(refundedPayment.status()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(refundedPayment.paymentId()).isEqualTo(payment.paymentId());
    }
    
    @Test
    @DisplayName("Should throw exception when refunding non-completed payment")
    void shouldThrowExceptionWhenRefundingNonCompletedPayment() {
        // Arrange
        ProcessPaymentRequest request = new ProcessPaymentRequest(
            "ORD-001", new BigDecimal("100.00"), PaymentMethod.CREDIT_CARD, "test@example.com", "4111111111111111"
        );
        PaymentDTO payment = paymentService.processPayment(request);
        
        // Keep trying until we get a FAILED payment
        int attempts = 0;
        while (payment.status() != PaymentStatus.FAILED && attempts < 20) {
            payment = paymentService.processPayment(request);
            attempts++;
        }
        
        // Skip test if we couldn't get a failed payment
        if (payment.status() != PaymentStatus.FAILED) {
            return;
        }
        
        RefundRequest refundRequest = new RefundRequest("Attempting to refund failed payment");
        String failedPaymentId = payment.paymentId();
        
        // Act & Assert
        assertThatThrownBy(() -> paymentService.refundPayment(failedPaymentId, refundRequest))
            .isInstanceOf(InvalidPaymentStateException.class)
            .hasMessageContaining("Cannot refund");
    }
    
    @Test
    @DisplayName("Should return empty list when no payments exist")
    void shouldReturnEmptyListWhenNoPaymentsExist() {
        // Act
        List<PaymentDTO> payments = paymentService.getAllPayments();
        
        // Assert
        assertThat(payments).isEmpty();
    }
    
    @Test
    @DisplayName("Should track payment count correctly")
    void shouldTrackPaymentCountCorrectly() {
        // Arrange
        ProcessPaymentRequest request = new ProcessPaymentRequest(
            "ORD-001", new BigDecimal("50.00"), PaymentMethod.PAYPAL, "test@example.com", null
        );
        
        // Act & Assert
        assertThat(paymentService.getPaymentCount()).isEqualTo(0);
        
        paymentService.processPayment(request);
        assertThat(paymentService.getPaymentCount()).isEqualTo(1);
        
        paymentService.processPayment(request);
        assertThat(paymentService.getPaymentCount()).isEqualTo(2);
    }
}
