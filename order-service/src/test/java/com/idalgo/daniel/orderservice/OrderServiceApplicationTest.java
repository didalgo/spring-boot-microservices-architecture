package com.idalgo.daniel.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Application context load test.
 * 
 * This test ensures that the Spring application context
 * can be loaded successfully with all beans configured correctly.
 * 
 * It's a simple but crucial "smoke test" that catches:
 * - Configuration errors
 * - Bean wiring issues
 * - Classpath problems
 * - Auto-configuration failures
 * 
 * @SpringBootTest loads the full application context
 * @ActiveProfiles("test") activates the "test" profile
 */
@SpringBootTest
@ActiveProfiles("test")
class OrderServiceApplicationTest {
    
    /**
     * Verifies that the application context loads successfully.
     * 
     * If this test fails, there's a fundamental configuration problem.
     */
    @Test
    void contextLoads() {
        // If context loads, test passes
        // No assertions needed - context loading is the test
    }
}
