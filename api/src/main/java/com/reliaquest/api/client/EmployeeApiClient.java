package com.reliaquest.api.client;

import com.reliaquest.api.config.RestClientConfig;
import com.reliaquest.api.exception.EmployeeServiceException;
import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * HTTP client service for communicating with the mock employee API.
 * Handles all external API calls with retry logic and error handling.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeApiClient {

    private final RestTemplate restTemplate;
    private final RestClientConfig restClientConfig;

    private static final String EMPLOYEES_ENDPOINT = "/api/v1/employee";
    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final long RETRY_DELAY_MS = 2000;

    /**
     * Fetch all employees from the mock API
     */
    public List<Employee> getAllEmployees() {
        return executeWithRetry(() -> {
            log.info("Fetching all employees from mock API");
            String url = restClientConfig.getBaseUrl() + EMPLOYEES_ENDPOINT;
            
            ResponseEntity<ApiResponse.EmployeeListResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse.EmployeeListResponse>() {}
            );

            ApiResponse.EmployeeListResponse apiResponse = response.getBody();
            if (apiResponse != null && apiResponse.isSuccess()) {
                log.info("Successfully fetched {} employees", apiResponse.getData().size());
                return apiResponse.getData();
            } else {
                throw new EmployeeServiceException("Failed to fetch employees: " + 
                    (apiResponse != null ? apiResponse.getError() : "Unknown error"));
            }
        });
    }

    /**
     * Fetch a single employee by ID
     */
    public Employee getEmployeeById(String id) {
        return executeWithRetry(() -> {
            log.info("Fetching employee with id: {}", id);
            String url = restClientConfig.getBaseUrl() + EMPLOYEES_ENDPOINT + "/" + id;
            
            ResponseEntity<ApiResponse.EmployeeResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse.EmployeeResponse>() {}
            );

            ApiResponse.EmployeeResponse apiResponse = response.getBody();
            if (apiResponse != null && apiResponse.isSuccess()) {
                log.info("Successfully fetched employee: {}", apiResponse.getData().getEmployeeName());
                return apiResponse.getData();
            } else {
                throw new EmployeeServiceException("Failed to fetch employee: " + 
                    (apiResponse != null ? apiResponse.getError() : "Unknown error"));
            }
        });
    }

    /**
     * Create a new employee
     */
    public Employee createEmployee(EmployeeInput employeeInput) {
        return executeWithRetry(() -> {
            log.info("Creating new employee: {}", employeeInput.getName());
            String url = restClientConfig.getBaseUrl() + EMPLOYEES_ENDPOINT;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<EmployeeInput> request = new HttpEntity<>(employeeInput, headers);
            
            ResponseEntity<ApiResponse.EmployeeResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<ApiResponse.EmployeeResponse>() {}
            );

            ApiResponse.EmployeeResponse apiResponse = response.getBody();
            if (apiResponse != null && apiResponse.isSuccess()) {
                log.info("Successfully created employee: {}", apiResponse.getData().getEmployeeName());
                return apiResponse.getData();
            } else {
                throw new EmployeeServiceException("Failed to create employee: " + 
                    (apiResponse != null ? apiResponse.getError() : "Unknown error"));
            }
        });
    }

    /**
     * Delete an employee by name (note: the mock API deletes by name, not ID)
     */
    public boolean deleteEmployeeByName(String name) {
        return executeWithRetry(() -> {
            log.info("Deleting employee with name: {}", name);
            String url = restClientConfig.getBaseUrl() + EMPLOYEES_ENDPOINT;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create request body with name (as per mock API requirement)
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", name);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<ApiResponse.BooleanResponse> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<ApiResponse.BooleanResponse>() {}
            );

            ApiResponse.BooleanResponse apiResponse = response.getBody();
            if (apiResponse != null && apiResponse.isSuccess()) {
                log.info("Successfully deleted employee: {}", name);
                return Boolean.TRUE.equals(apiResponse.getData());
            } else {
                throw new EmployeeServiceException("Failed to delete employee: " + 
                    (apiResponse != null ? apiResponse.getError() : "Unknown error"));
            }
        });
    }

    /**
     * Execute a supplier with retry logic
     * Implements exponential backoff for failed requests
     */
    private <T> T executeWithRetry(Supplier<T> operation) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                return operation.get();
            } catch (RestClientException e) {
                lastException = e;
                log.warn("Attempt {} failed: {}", attempt, e.getMessage());
                
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    try {
                        // Enhanced exponential backoff with jitter for rate limiting
                        long baseDelay = RETRY_DELAY_MS * attempt;
                        long jitter = (long) (Math.random() * 1000); // Add randomness
                        long delay = baseDelay + jitter;
                        
                        log.info("Rate limited (429) - Retrying in {} ms... (attempt {}/{})", 
                                delay, attempt, MAX_RETRY_ATTEMPTS);
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new EmployeeServiceException("Retry interrupted", ie);
                    }
                }
            }
        }
        
        log.error("All {} attempts failed", MAX_RETRY_ATTEMPTS);
        throw new EmployeeServiceException("Failed to execute request after " + MAX_RETRY_ATTEMPTS + " attempts", lastException);
    }
}