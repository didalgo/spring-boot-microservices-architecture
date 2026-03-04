package com.idalgo.daniel.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Configuration for RestClient used to communicate with other services.
 * 
 * Uses Eureka Service Discovery for dynamic service resolution.
 * Includes timeout configuration and load balancing support.
 */
@Configuration
public class RestClientConfig {
    
    @Value("${payment-service.name}")
    private String paymentServiceName;
    
    /**
     * Creates LoadBalanced RestClient.Builder for service discovery.
     * 
     * The @LoadBalanced annotation enables:
     * - Service name resolution via Eureka
     * - Client-side load balancing
     * - Automatic failover between instances
     * 
     * @return RestClient.Builder with load balancing enabled
     */
    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
        
        return RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory(httpClient));
    }
    
    /**
     * Creates RestClient for Payment Service communication.
     * 
     * Uses service name (payment-service) instead of hardcoded URL.
     * Eureka resolves the service name to actual instance(s).
     * 
     * @param builder LoadBalanced RestClient.Builder
     * @return Configured RestClient for Payment Service
     */
    @Bean
    public RestClient paymentServiceRestClient(RestClient.Builder builder) {
        return builder
            .baseUrl("http://" + paymentServiceName)
            .build();
    }
}
