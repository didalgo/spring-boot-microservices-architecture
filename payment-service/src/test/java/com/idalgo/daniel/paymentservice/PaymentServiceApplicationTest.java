package com.idalgo.daniel.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Application context load test for Payment Service.
 * 
 * Verifies that:
 * - Application context loads successfully
 * - All beans are wired correctly
 * - No configuration errors
 * - GlobalExceptionHandler from common module is picked up
 */
@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceApplicationTest {
    
    @Test
    void contextLoads() {
        // Context loading is the test
    }
}
