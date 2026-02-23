package com.idalgo.daniel.paymentservice.controller;

import com.idalgo.daniel.contracts.dto.payment.*;
import com.idalgo.daniel.paymentservice.service.PaymentService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PaymentController.
 * 
 * Note: Exception handling tests are intentionally limited.
 * GlobalExceptionHandler integration will be tested with full
 * @SpringBootTest in integration test suites.
 */
@WebMvcTest(PaymentController.class)
@DisplayName("PaymentController Integration Tests")
class PaymentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private PaymentService paymentService;
    
    @Test
    @DisplayName("Should process payment successfully")
    void shouldProcessPaymentSuccessfully() throws Exception {
        // Arrange
        ProcessPaymentRequest request = new ProcessPaymentRequest(
            "ORD-001",
            new BigDecimal("99.99"),
            PaymentMethod.CREDIT_CARD,
            "customer@example.com",
            "4111111111111111"
        );
        
        PaymentDTO response = new PaymentDTO(
            "PAY-123",
            "ORD-001",
            new BigDecimal("99.99"),
            PaymentMethod.CREDIT_CARD,
            PaymentStatus.COMPLETED,
            "customer@example.com",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(paymentService.processPayment(any(ProcessPaymentRequest.class)))
            .thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.paymentId").value("PAY-123"))
            .andExpect(jsonPath("$.orderId").value("ORD-001"))
            .andExpect(jsonPath("$.amount").value(99.99))
            .andExpect(jsonPath("$.status").value("COMPLETED"));
        
        verify(paymentService, times(1)).processPayment(any(ProcessPaymentRequest.class));
    }
    
    @Test
    @DisplayName("Should return 400 when validation fails")
    void shouldReturn400WhenValidationFails() throws Exception {
        // Arrange - invalid request
        ProcessPaymentRequest invalidRequest = new ProcessPaymentRequest(
            "",  // blank orderId
            new BigDecimal("-10"),  // negative amount
            PaymentMethod.CREDIT_CARD,
            "invalid-email",  // invalid email
            "123"  // invalid card number
        );
        
        // Act & Assert
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
        
        verify(paymentService, never()).processPayment(any());
    }
    
    @Test
    @DisplayName("Should retrieve all payments")
    void shouldRetrieveAllPayments() throws Exception {
        // Arrange
        List<PaymentDTO> payments = List.of(
            new PaymentDTO(
                "PAY-001", "ORD-001", new BigDecimal("50.00"),
                PaymentMethod.PAYPAL, PaymentStatus.COMPLETED,
                "test1@example.com", LocalDateTime.now(), LocalDateTime.now()
            ),
            new PaymentDTO(
                "PAY-002", "ORD-002", new BigDecimal("100.00"),
                PaymentMethod.CREDIT_CARD, PaymentStatus.COMPLETED,
                "test2@example.com", LocalDateTime.now(), LocalDateTime.now()
            )
        );
        
        when(paymentService.getAllPayments()).thenReturn(payments);
        
        // Act & Assert
        mockMvc.perform(get("/api/payments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].paymentId").value("PAY-001"))
            .andExpect(jsonPath("$[1].paymentId").value("PAY-002"));
        
        verify(paymentService, times(1)).getAllPayments();
    }
    
    @Test
    @DisplayName("Should retrieve payment by ID")
    void shouldRetrievePaymentById() throws Exception {
        // Arrange
        PaymentDTO response = new PaymentDTO(
            "PAY-123",
            "ORD-001",
            new BigDecimal("99.99"),
            PaymentMethod.CREDIT_CARD,
            PaymentStatus.COMPLETED,
            "customer@example.com",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(paymentService.getPaymentById("PAY-123")).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(get("/api/payments/PAY-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentId").value("PAY-123"))
            .andExpect(jsonPath("$.orderId").value("ORD-001"));
        
        verify(paymentService, times(1)).getPaymentById("PAY-123");
    }
    
    @Test
    @DisplayName("Should refund payment successfully")
    void shouldRefundPaymentSuccessfully() throws Exception {
        // Arrange
        RefundRequest request = new RefundRequest("Customer requested refund");
        
        PaymentDTO response = new PaymentDTO(
            "PAY-123",
            "ORD-001",
            new BigDecimal("99.99"),
            PaymentMethod.CREDIT_CARD,
            PaymentStatus.REFUNDED,
            "customer@example.com",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(paymentService.refundPayment(eq("PAY-123"), any(RefundRequest.class)))
            .thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(put("/api/payments/PAY-123/refund")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentId").value("PAY-123"))
            .andExpect(jsonPath("$.status").value("REFUNDED"));
        
        verify(paymentService, times(1)).refundPayment(eq("PAY-123"), any(RefundRequest.class));
    }
    
    @Test
    @DisplayName("Should return empty array when no payments exist")
    void shouldReturnEmptyArrayWhenNoPaymentsExist() throws Exception {
        // Arrange
        when(paymentService.getAllPayments()).thenReturn(List.of());
        
        // Act & Assert
        mockMvc.perform(get("/api/payments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
        
        verify(paymentService, times(1)).getAllPayments();
    }
}
