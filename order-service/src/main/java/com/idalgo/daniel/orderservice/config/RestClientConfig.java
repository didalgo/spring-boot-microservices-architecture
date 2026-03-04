package com.idalgo.daniel.orderservice.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.observation.ClientRequestObservationConvention;
import org.springframework.http.client.observation.DefaultClientRequestObservationConvention;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Configuration for RestClient used to communicate with other services.
 *
 * Uses Eureka Service Discovery for dynamic service resolution.
 * Includes timeout configuration, load balancing, and tracing propagation.
 */
@Configuration
public class RestClientConfig {

    @Value("${payment-service.name}")
    private String paymentServiceName;

    private final ObservationRegistry observationRegistry;

    public RestClientConfig(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    /**
     * Creates LoadBalanced RestClient.Builder for service discovery.
     *
     * The @LoadBalanced annotation enables:
     * - Service name resolution via Eureka
     * - Client-side load balancing
     * - Automatic failover between instances
     * - Tracing context propagation via ObservationRegistry
     *
     * @return RestClient.Builder with load balancing and observability enabled
     */
    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(5))
                .withReadTimeout(Duration.ofSeconds(5));

        return RestClient.builder()
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .observationRegistry(observationRegistry);
    }

    /**
     * Creates RestClient for Payment Service communication.
     *
     * Uses service name (payment-service) instead of hardcoded URL.
     * Eureka resolves the service name to actual instance(s).
     *
     * @param builder LoadBalanced RestClient.Builder with observability
     * @return Configured RestClient for Payment Service
     */
    @Bean
    public RestClient paymentServiceRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("http://" + paymentServiceName)
                .build();
    }
}