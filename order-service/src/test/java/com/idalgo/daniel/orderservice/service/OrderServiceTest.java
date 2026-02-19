package com.idalgo.daniel.orderservice.service;

import com.idalgo.daniel.orderservice.config.OrderServiceConfig;
import com.idalgo.daniel.orderservice.dto.CreateOrderRequest;
import com.idalgo.daniel.orderservice.dto.OrderResponse;
import com.idalgo.daniel.orderservice.exception.OrderNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for OrderService.
 * 
 * These are "pure" unit tests:
 * - No Spring context loaded
 * - No external dependencies
 * - Fast execution
 * - Focused on business logic
 * 
 * We use AssertJ for fluent assertions (included in spring-boot-starter-test).
 * 
 * Test naming convention: should{ExpectedBehavior}When{StateUnderTest}
 */
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {
    
    private OrderService orderService;
    private OrderServiceConfig config;
    
    /**
     * Setup method run before each test.
     * 
     * Creates a fresh OrderService instance with test configuration.
     */
    @BeforeEach
    void setUp() {
        // Create test configuration
        config = new OrderServiceConfig(
            100,           // maxOrdersPerCustomer
            "TEST",        // orderPrefix
            false          // enableNotifications
        );
        
        // Create service with test config
        orderService = new OrderService(config);
    }
    
    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrderSuccessfully() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST-001",
            "PROD-001",
            2,
            new BigDecimal("199.98")
        );
        
        // Act
        OrderResponse response = orderService.createOrder(request);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.orderId()).isNotNull();
        assertThat(response.orderId()).startsWith("TEST-");
        assertThat(response.customerId()).isEqualTo("CUST-001");
        assertThat(response.productId()).isEqualTo("PROD-001");
        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("199.98"));
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.createdAt()).isNotNull();
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
    
    @Test
    @DisplayName("Should track order count correctly")
    void shouldTrackOrderCountCorrectly() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST-001", "PROD-001", 1, new BigDecimal("99.99")
        );
        
        // Act & Assert
        assertThat(orderService.getOrderCount()).isEqualTo(0);
        
        OrderResponse order1 = orderService.createOrder(request);
        assertThat(orderService.getOrderCount()).isEqualTo(1);
        
        orderService.createOrder(request);
        assertThat(orderService.getOrderCount()).isEqualTo(2);
        
        orderService.deleteOrder(order1.orderId());
        assertThat(orderService.getOrderCount()).isEqualTo(1);
    }
}
