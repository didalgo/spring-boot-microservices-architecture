package com.idalgo.daniel.orderservice.controller;

import com.idalgo.daniel.contracts.dto.order.OrderStatus;
import com.idalgo.daniel.orderservice.dto.CreateOrderRequest;
import com.idalgo.daniel.orderservice.dto.OrderResponse;
import com.idalgo.daniel.orderservice.service.OrderService;

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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OrderController.
 * 
 * Updated in Lesson 4 to use new OrderResponse with status and paymentId.
 */
@WebMvcTest(OrderController.class)
@DisplayName("OrderController Integration Tests")
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private OrderService orderService;
    
    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrderSuccessfully() throws Exception {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST-001",
            "PROD-001",
            2,
            new BigDecimal("199.98")
        );
        
        OrderResponse response = new OrderResponse(
            "ORD-123",
            "CUST-001",
            "PROD-001",
            2,
            new BigDecimal("199.98"),
            OrderStatus.CONFIRMED,
            "PAY-456",
            LocalDateTime.now()
        );
        
        when(orderService.createOrder(any(CreateOrderRequest.class)))
            .thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderId").value("ORD-123"))
            .andExpect(jsonPath("$.customerId").value("CUST-001"))
            .andExpect(jsonPath("$.productId").value("PROD-001"))
            .andExpect(jsonPath("$.quantity").value(2))
            .andExpect(jsonPath("$.totalAmount").value(199.98))
            .andExpect(jsonPath("$.status").value("CONFIRMED"))
            .andExpect(jsonPath("$.paymentId").value("PAY-456"))
            .andExpect(jsonPath("$.createdAt").exists());
        
        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class));
    }
    
    @Test
    @DisplayName("Should return 400 when request validation fails")
    void shouldReturn400WhenValidationFails() throws Exception {
        // Arrange - invalid request (missing required fields)
        CreateOrderRequest invalidRequest = new CreateOrderRequest(
            "",  // blank customerId
            "PROD-001",
            -1,  // negative quantity
            new BigDecimal("199.98")
        );
        
        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
        
        verify(orderService, never()).createOrder(any());
    }
    
    @Test
    @DisplayName("Should retrieve all orders")
    void shouldRetrieveAllOrders() throws Exception {
        // Arrange
        List<OrderResponse> orders = List.of(
            new OrderResponse(
                "ORD-001", "CUST-001", "PROD-001", 1,
                new BigDecimal("50.00"), OrderStatus.CONFIRMED, "PAY-001", LocalDateTime.now()
            ),
            new OrderResponse(
                "ORD-002", "CUST-002", "PROD-002", 2,
                new BigDecimal("100.00"), OrderStatus.PAYMENT_FAILED, null, LocalDateTime.now()
            )
        );
        
        when(orderService.getAllOrders()).thenReturn(orders);
        
        // Act & Assert
        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].orderId").value("ORD-001"))
            .andExpect(jsonPath("$[1].orderId").value("ORD-002"));
        
        verify(orderService, times(1)).getAllOrders();
    }
    
    @Test
    @DisplayName("Should retrieve order by ID")
    void shouldRetrieveOrderById() throws Exception {
        // Arrange
        OrderResponse response = new OrderResponse(
            "ORD-123",
            "CUST-001",
            "PROD-001",
            2,
            new BigDecimal("199.98"),
            OrderStatus.CONFIRMED,
            "PAY-456",
            LocalDateTime.now()
        );
        
        when(orderService.getOrderById("ORD-123")).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(get("/api/orders/ORD-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value("ORD-123"))
            .andExpect(jsonPath("$.customerId").value("CUST-001"));
        
        verify(orderService, times(1)).getOrderById("ORD-123");
    }
    
    @Test
    @DisplayName("Should delete order successfully")
    void shouldDeleteOrderSuccessfully() throws Exception {
        // Arrange
        doNothing().when(orderService).deleteOrder("ORD-123");
        
        // Act & Assert
        mockMvc.perform(delete("/api/orders/ORD-123"))
            .andExpect(status().isNoContent());
        
        verify(orderService, times(1)).deleteOrder("ORD-123");
    }
    
    @Test
    @DisplayName("Should return empty array when no orders exist")
    void shouldReturnEmptyArrayWhenNoOrdersExist() throws Exception {
        // Arrange
        when(orderService.getAllOrders()).thenReturn(List.of());
        
        // Act & Assert
        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
        
        verify(orderService, times(1)).getAllOrders();
    }
}
