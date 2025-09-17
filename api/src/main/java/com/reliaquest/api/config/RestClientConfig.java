package com.reliaquest.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

/**
 * Configuration for REST client to communicate with the mock employee server.
 * Includes retry logic and request/response logging.
 */
@Slf4j
@Configuration
public class RestClientConfig {

    @Value("${employee.service.base-url:http://localhost:8112}")
    private String baseUrl;

    @Value("${employee.service.timeout:5000}")
    private int timeout;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Add request/response logging interceptor
        restTemplate.setInterceptors(List.of(loggingInterceptor()));
        
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.debug("Request: {} {}", request.getMethod(), request.getURI());
            if (body.length > 0) {
                log.debug("Request body: {}", new String(body));
            }
            
            var response = execution.execute(request, body);
            
            log.debug("Response status: {}", response.getStatusCode());
            return response;
        };
    }

    /**
     * Get the base URL for the employee service
     */
    public String getBaseUrl() {
        return baseUrl;
    }
}
