package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic response wrapper for API responses from the mock server.
 * Handles both single objects and lists of objects.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private T data;
    private String status;
    private String error;

    /**
     * Check if the response indicates success
     */
    public boolean isSuccess() {
        return error == null && status != null && status.contains("Successfully");
    }

    /**
     * Specialized response for list of employees
     */
    public static class EmployeeListResponse extends ApiResponse<List<Employee>> {
    }

    /**
     * Specialized response for single employee
     */
    public static class EmployeeResponse extends ApiResponse<Employee> {
    }

    /**
     * Specialized response for boolean operations (like delete)
     */
    public static class BooleanResponse extends ApiResponse<Boolean> {
    }
}

