package com.idalgo.daniel.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Configuration for RestClient beans.
 *
 * Configures HTTP client with:
 * - Connection timeout: 5 seconds
 * - Read timeout: 10 seconds (configurable per request)
 */
@Configuration
public class RestClientConfig {

    @Value("${payment-service.url}")
    private String paymentServiceUrl;

    @Bean
    public RestClient paymentServiceRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        return RestClient.builder()
                .baseUrl(paymentServiceUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }
}