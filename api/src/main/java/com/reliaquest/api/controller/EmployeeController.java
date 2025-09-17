package com.reliaquest.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for employee operations.
 * Implements the IEmployeeController interface to provide all required endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;

    /**
     * Get all employees
     * 
     * @return ResponseEntity containing list of all employees
     */
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("GET /api/v1/employee - Fetching all employees");
        
        List<Employee> employees = employeeService.getAllEmployees();
        
        log.info("Successfully retrieved {} employees", employees.size());
        return ResponseEntity.ok(employees);
    }

    /**
     * Search employees by name fragment
     * 
     * @param searchString the name fragment to search for
     * @return ResponseEntity containing list of matching employees
     */
    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        log.info("GET /api/v1/employee/search/{} - Searching employees by name", searchString);
        
        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
        
        log.info("Found {} employees matching search term: {}", employees.size(), searchString);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employee by ID
     * 
     * @param id the employee ID
     * @return ResponseEntity containing the employee
     */
    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        log.info("GET /api/v1/employee/{} - Fetching employee by id", id);
        
        Employee employee = employeeService.getEmployeeById(id);
        
        log.info("Successfully retrieved employee: {}", employee.getEmployeeName());
        return ResponseEntity.ok(employee);
    }

    /**
     * Get the highest salary among all employees
     * 
     * @return ResponseEntity containing the highest salary
     */
    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("GET /api/v1/employee/highestSalary - Finding highest salary");
        
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        
        log.info("Highest salary found: {}", highestSalary);
        return ResponseEntity.ok(highestSalary);
    }

    /**
     * Get names of top 10 highest earning employees
     * 
     * @return ResponseEntity containing list of employee names
     */
    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("GET /api/v1/employee/topTenHighestEarningEmployeeNames - Finding top 10 earners");
        
        List<String> topEarners = employeeService.getTopTenHighestEarningEmployeeNames();
        
        log.info("Found {} top earning employees", topEarners.size());
        return ResponseEntity.ok(topEarners);
    }

    /**
     * Create a new employee
     * 
     * @param employeeInput the employee data to create
     * @return ResponseEntity containing the created employee
     */
    @Override
    public ResponseEntity<Employee> createEmployee(@Valid EmployeeInput employeeInput) {
        log.info("POST /api/v1/employee - Creating new employee: {}", employeeInput.getName());
        
        Employee createdEmployee = employeeService.createEmployee(employeeInput);
        
        log.info("Successfully created employee: {} with id: {}", 
                createdEmployee.getEmployeeName(), createdEmployee.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    /**
     * Delete employee by ID
     * 
     * @param id the employee ID to delete
     * @return ResponseEntity containing the name of the deleted employee
     */
    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        log.info("DELETE /api/v1/employee/{} - Deleting employee by id", id);
        
        String deletedEmployeeName = employeeService.deleteEmployeeById(id);
        
        log.info("Successfully deleted employee: {}", deletedEmployeeName);
        
        try {
            // Use ObjectMapper to properly serialize the string as JSON
            String jsonResponse = objectMapper.writeValueAsString(deletedEmployeeName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse);
        } catch (JsonProcessingException e) {
            log.error("Error serializing response to JSON", e);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("\"" + deletedEmployeeName.replace("\"", "\\\"") + "\"");
        }
    }
}
