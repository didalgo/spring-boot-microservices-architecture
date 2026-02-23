package com.idalgo.daniel.orderservice.service;

import com.idalgo.daniel.contracts.dto.order.OrderStatus;
import com.idalgo.daniel.contracts.dto.payment.PaymentDTO;
import com.idalgo.daniel.contracts.dto.payment.PaymentMethod;
import com.idalgo.daniel.contracts.dto.payment.PaymentStatus;
import com.idalgo.daniel.orderservice.client.PaymentClient;
import com.idalgo.daniel.orderservice.client.PaymentServiceException;
import com.idalgo.daniel.orderservice.config.OrderServiceConfig;
import com.idalgo.daniel.orderservice.dto.CreateOrderRequest;
import com.idalgo.daniel.orderservice.dto.OrderResponse;
import com.idalgo.daniel.orderservice.exception.OrderNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService.
 * 
 * Updated in Lesson 4 to mock PaymentClient.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {
    
    private OrderService orderService;
    private OrderServiceConfig config;
    
    @Mock
    private PaymentClient paymentClient;
    
    @BeforeEach
    void setUp() {
        config = new OrderServiceConfig(
            100,
            "TEST",
            false
        );
        
        orderService = new OrderService(config, paymentClient);
    }
    
    @Test
    @DisplayName("Should create order with CONFIRMED status when payment succeeds")
    void shouldCreateOrderWithConfirmedStatusWhenPaymentSucceeds() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST-001",
            "PROD-001",
            2,
            new BigDecimal("199.98")
        );
        
        PaymentDTO successfulPayment = new PaymentDTO(
            "PAY-123",
            "ORD-xxx",
            new BigDecimal("199.98"),
            PaymentMethod.CREDIT_CARD,
            PaymentStatus.COMPLETED,
            "customer@example.com",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(paymentClient.processPayment(any())).thenReturn(successfulPayment);
        
        // Act
        OrderResponse response = orderService.createOrder(request);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.orderId()).startsWith("TEST-");
        assertThat(response.customerId()).isEqualTo("CUST-001");
        assertThat(response.productId()).isEqualTo("PROD-001");
        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("199.98"));
        assertThat(response.status()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(response.paymentId()).isEqualTo("PAY-123");
        assertThat(response.createdAt()).isNotNull();
        
        verify(paymentClient, times(1)).processPayment(any());
    }
    
    @Test
    @DisplayName("Should create order with PAYMENT_FAILED status when payment fails")
    void shouldCreateOrderWithPaymentFailedStatusWhenPaymentFails() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST-001",
            "PROD-001",
            1,
            new BigDecimal("50.00")
        );
        
        PaymentDTO failedPayment = new PaymentDTO(
            "PAY-456",
            "ORD-xxx",
            new BigDecimal("50.00"),
            PaymentMethod.CREDIT_CARD,
            PaymentStatus.FAILED,
            "customer@example.com",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(paymentClient.processPayment(any())).thenReturn(failedPayment);
        
        // Act
        OrderResponse response = orderService.createOrder(request);
        
        // Assert
        assertThat(response.status()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        assertThat(response.paymentId()).isEqualTo("PAY-456");
    }
    
    @Test
    @DisplayName("Should create order with PAYMENT_FAILED status when payment service is unavailable")
    void shouldCreateOrderWithPaymentFailedStatusWhenPaymentServiceUnavailable() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST-001",
            "PROD-001",
            1,
            new BigDecimal("75.00")
        );
        
        when(paymentClient.processPayment(any()))
            .thenThrow(new PaymentServiceException("Service unavailable"));
        
        // Act
        OrderResponse response = orderService.createOrder(request);
        
        // Assert
        assertThat(response.status()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        assertThat(response.paymentId()).isNull();
    }
    
    @Test
    @DisplayName("Should generate unique order IDs")
    void shouldGenerateUniqueOrderIds() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST-001",
            "PROD-001",
            1,
            new BigDecimal("99.99")
        );
        
        PaymentDTO payment = new PaymentDTO(
            "PAY-1",
            "ORD-xxx",
            new BigDecimal("99.99"),
            PaymentMethod.PAYPAL,
            PaymentStatus.COMPLETED,
            "customer@example.com",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(paymentClient.processPayment(any())).thenReturn(payment);
        
        // Act
        OrderResponse order1 = orderService.createOrder(request);
        OrderResponse order2 = orderService.createOrder(request);
        OrderResponse order3 = orderService.createOrder(request);
        
        // Assert
        assertThat(order1.orderId()).isNotEqualTo(order2.orderId());
        assertThat(order2.orderId()).isNotEqualTo(order3.orderId());
        assertThat(order1.orderId()).isNotEqualTo(order3.orderId());
    }
    
    @Test
    @DisplayName("Should retrieve all orders")
    void shouldRetrieveAllOrders() {
        // Arrange
        CreateOrderRequest request1 = new CreateOrderRequest(
            "CUST-001", "PROD-001", 1, new BigDecimal("50.00")
        );
        CreateOrderRequest request2 = new CreateOrderRequest(
            "CUST-002", "PROD-002", 2, new BigDecimal("100.00")
        );
        
        PaymentDTO payment = new PaymentDTO(
            "PAY-1",
            "ORD-xxx",
            new BigDecimal("50.00"),
            PaymentMethod.PAYPAL,
            PaymentStatus.COMPLETED,
            "customer@example.com",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(paymentClient.processPayment(any())).thenReturn(payment);
        
        orderService.createOrder(request1);
        orderService.createOrder(request2);
        
        // Act
        List<OrderResponse> orders = orderService.getAllOrders();
        
        // Assert
        assertThat(orders).hasSize(2);
        assertThat(orders).extracting(OrderResponse::customerId)
            .containsExactlyInAnyOrder("CUST-001", "CUST-002");
    }
    
    @Test
    @DisplayName("Should retrieve order by ID")
    void shouldRetrieveOrderById() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST-001", "PROD-001", 1, new BigDecimal("99.99")
        );
        
        PaymentDTO payment = new PaymentDTO(
            "PAY-1",
            "ORD-xxx",
            new BigDecimal("99.99"),
            PaymentMethod.CREDIT_CARD,
            PaymentStatus.COMPLETED,
            "customer@example.com",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(paymentClient.processPayment(any())).thenReturn(payment);
        
        OrderResponse createdOrder = orderService.createOrder(request);
        
        // Act
        OrderResponse retrievedOrder = orderService.getOrderById(createdOrder.orderId());
        
        // Assert
        assertThat(retrievedOrder).isEqualTo(createdOrder);
    }
    
    @Test
    @DisplayName("Should throw exception when order not found")
    void shouldThrowExceptionWhenOrderNotFound() {
        // Act & Assert
        assertThatThrownBy(() -> orderService.getOrderById("NON-EXISTENT"))
            .isInstanceOf(OrderNotFoundException.class)
            .hasMessageContaining("NON-EXISTENT");
    }
    
    @Test
    @DisplayName("Should delete order successfully")
    void shouldDeleteOrderSuccessfully() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST-001", "PROD-001", 1, new BigDecimal("99.99")
        );
        
        PaymentDTO payment = new PaymentDTO(
            "PAY-1",
            "ORD-xxx",
            new BigDecimal("99.99"),
            PaymentMethod.CREDIT_CARD,
            PaymentStatus.COMPLETED,
            "customer@example.com",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(paymentClient.processPayment(any())).thenReturn(payment);
        
        OrderResponse createdOrder = orderService.createOrder(request);
        
        // Act
        orderService.deleteOrder(createdOrder.orderId());
        
        // Assert
        assertThatThrownBy(() -> orderService.getOrderById(createdOrder.orderId()))
            .isInstanceOf(OrderNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should throw exception when deleting non-existent order")
    void shouldThrowExceptionWhenDeletingNonExistentOrder() {
        // Act & Assert
        assertThatThrownBy(() -> orderService.deleteOrder("NON-EXISTENT"))
            .isInstanceOf(OrderNotFoundException.class)
            .hasMessageContaining("NON-EXISTENT");
    }
    
    @Test
    @DisplayName("Should return empty list when no orders exist")
    void shouldReturnEmptyListWhenNoOrdersExist() {
        // Act
        List<OrderResponse> orders = orderService.getAllOrders();
        
        // Assert
        assertThat(orders).isEmpty();
    }
}
