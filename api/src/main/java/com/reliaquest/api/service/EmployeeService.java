package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for employee operations.
 * Contains business logic for processing employee data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeApiClient employeeApiClient;

    /**
     * Get all employees
     */
    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeApiClient.getAllEmployees();
    }

    /**
     * Search employees by name fragment
     */
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        log.info("Searching employees by name: {}", searchString);
        
        List<Employee> allEmployees = employeeApiClient.getAllEmployees();
        
        return allEmployees.stream()
                .filter(employee -> employee.getEmployeeName() != null && 
                        employee.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Get employee by ID
     */
    public Employee getEmployeeById(String id) {
        log.info("Fetching employee by id: {}", id);
        
        try {
            return employeeApiClient.getEmployeeById(id);
        } catch (Exception e) {
            log.error("Employee not found with id: {}", id);
            throw new EmployeeNotFoundException(id);
        }
    }

    /**
     * Get the highest salary among all employees
     */
    public Integer getHighestSalaryOfEmployees() {
        log.info("Finding highest salary among all employees");
        
        List<Employee> allEmployees = employeeApiClient.getAllEmployees();
        
        return allEmployees.stream()
                .filter(employee -> employee.getEmployeeSalary() != null)
                .mapToInt(Employee::getEmployeeSalary)
                .max()
                .orElse(0);
    }

    /**
     * Get names of top 10 highest earning employees
     */
    public List<String> getTopTenHighestEarningEmployeeNames() {
        log.info("Finding top 10 highest earning employees");
        
        List<Employee> allEmployees = employeeApiClient.getAllEmployees();
        
        return allEmployees.stream()
                .filter(employee -> employee.getEmployeeSalary() != null)
                .sorted(Comparator.comparing(Employee::getEmployeeSalary).reversed())
                .limit(10)
                .map(Employee::getEmployeeName)
                .collect(Collectors.toList());
    }

    /**
     * Create a new employee
     */
    public Employee createEmployee(EmployeeInput employeeInput) {
        log.info("Creating new employee: {}", employeeInput.getName());
        return employeeApiClient.createEmployee(employeeInput);
    }

    /**
     * Delete employee by ID
     * Note: This requires finding the employee first to get the name,
     * since the mock API deletes by name, not ID
     */
    public String deleteEmployeeById(String id) {
        log.info("Deleting employee by id: {}", id);
        
        // First, get the employee to find their name
        Employee employee = getEmployeeById(id);
        String employeeName = employee.getEmployeeName();
        
        // Delete using the name
        boolean deleted = employeeApiClient.deleteEmployeeByName(employeeName);
        
        if (deleted) {
            log.info("Successfully deleted employee: {}", employeeName);
            return employeeName;
        } else {
            throw new EmployeeNotFoundException("Failed to delete employee with id: " + id);
        }
    }
}

