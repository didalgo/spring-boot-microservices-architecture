package com.idalgo.daniel.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
//@Import(TestSecurityConfig.class)
class ApiGatewayApplicationTest {

    @Test
    void contextLoads() {
        // Context loading is the test
    }
}