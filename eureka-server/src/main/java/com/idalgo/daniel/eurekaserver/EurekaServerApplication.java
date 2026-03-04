package com.idalgo.daniel.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Server Application.
 *
 * Service Discovery server for the microservices architecture.
 *
 * Features:
 * - Service registration and discovery
 * - Health checking
 * - Load balancing support
 * - Web dashboard at http://localhost:8761
 *
 * Port: 8761 (Eureka default)
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}