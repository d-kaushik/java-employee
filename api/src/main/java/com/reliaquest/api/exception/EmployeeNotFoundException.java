package com.reliaquest.api.exception;

/**
 * Exception thrown when an employee is not found.
 */
public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String id) {
        super("Employee not found with id: " + id);
    }

    public EmployeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

