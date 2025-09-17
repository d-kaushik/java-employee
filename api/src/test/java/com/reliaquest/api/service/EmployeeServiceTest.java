package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for EmployeeService
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeApiClient employeeApiClient;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;
    private Employee employee3;
    private List<Employee> allEmployees;

    @BeforeEach
    void setUp() {
        employee1 = Employee.builder()
                .id("1")
                .employeeName("John Doe")
                .employeeSalary(75000)
                .employeeAge(30)
                .employeeTitle("Developer")
                .employeeEmail("john@company.com")
                .build();

        employee2 = Employee.builder()
                .id("2")
                .employeeName("Jane Smith")
                .employeeSalary(85000)
                .employeeAge(28)
                .employeeTitle("Senior Developer")
                .employeeEmail("jane@company.com")
                .build();

        employee3 = Employee.builder()
                .id("3")
                .employeeName("Bob Johnson")
                .employeeSalary(95000)
                .employeeAge(35)
                .employeeTitle("Tech Lead")
                .employeeEmail("bob@company.com")
                .build();

        allEmployees = Arrays.asList(employee1, employee2, employee3);
    }

    @Test
    void getAllEmployees_ShouldReturnAllEmployees() {
        // Given
        when(employeeApiClient.getAllEmployees()).thenReturn(allEmployees);

        // When
        List<Employee> result = employeeService.getAllEmployees();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(allEmployees);
    }

    @Test
    void getEmployeesByNameSearch_ShouldReturnMatchingEmployees() {
        // Given
        when(employeeApiClient.getAllEmployees()).thenReturn(allEmployees);

        // When
        List<Employee> result = employeeService.getEmployeesByNameSearch("John");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Employee::getEmployeeName)
                .containsExactlyInAnyOrder("John Doe", "Bob Johnson");
    }

    @Test
    void getEmployeesByNameSearch_CaseInsensitive_ShouldReturnMatchingEmployees() {
        // Given
        when(employeeApiClient.getAllEmployees()).thenReturn(allEmployees);

        // When
        List<Employee> result = employeeService.getEmployeesByNameSearch("JANE");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmployeeName()).isEqualTo("Jane Smith");
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee() {
        // Given
        when(employeeApiClient.getEmployeeById("1")).thenReturn(employee1);

        // When
        Employee result = employeeService.getEmployeeById("1");

        // Then
        assertThat(result).isEqualTo(employee1);
    }

    @Test
    void getEmployeeById_WhenNotFound_ShouldThrowException() {
        // Given
        when(employeeApiClient.getEmployeeById("999")).thenThrow(new RuntimeException("Not found"));

        // When & Then
        assertThatThrownBy(() -> employeeService.getEmployeeById("999"))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getHighestSalaryOfEmployees_ShouldReturnHighestSalary() {
        // Given
        when(employeeApiClient.getAllEmployees()).thenReturn(allEmployees);

        // When
        Integer result = employeeService.getHighestSalaryOfEmployees();

        // Then
        assertThat(result).isEqualTo(95000);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnTopEarners() {
        // Given
        when(employeeApiClient.getAllEmployees()).thenReturn(allEmployees);

        // When
        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("Bob Johnson", "Jane Smith", "John Doe");
    }

    @Test
    void createEmployee_ShouldReturnCreatedEmployee() {
        // Given
        EmployeeInput input = EmployeeInput.builder()
                .name("New Employee")
                .salary(70000)
                .age(25)
                .title("Junior Developer")
                .build();

        Employee createdEmployee = Employee.builder()
                .id("4")
                .employeeName("New Employee")
                .employeeSalary(70000)
                .employeeAge(25)
                .employeeTitle("Junior Developer")
                .employeeEmail("new@company.com")
                .build();

        when(employeeApiClient.createEmployee(input)).thenReturn(createdEmployee);

        // When
        Employee result = employeeService.createEmployee(input);

        // Then
        assertThat(result).isEqualTo(createdEmployee);
    }

    @Test
    void deleteEmployeeById_ShouldReturnDeletedEmployeeName() {
        // Given
        when(employeeApiClient.getEmployeeById("1")).thenReturn(employee1);
        when(employeeApiClient.deleteEmployeeByName("John Doe")).thenReturn(true);

        // When
        String result = employeeService.deleteEmployeeById("1");

        // Then
        assertThat(result).isEqualTo("John Doe");
    }

    @Test
    void deleteEmployeeById_WhenEmployeeNotFound_ShouldThrowException() {
        // Given
        when(employeeApiClient.getEmployeeById("999")).thenThrow(new RuntimeException("Not found"));

        // When & Then
        assertThatThrownBy(() -> employeeService.deleteEmployeeById("999"))
                .isInstanceOf(EmployeeNotFoundException.class);
    }
}

