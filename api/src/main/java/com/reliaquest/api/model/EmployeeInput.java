package com.reliaquest.api.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input model for creating new employees.
 * Matches the validation requirements of the mock server.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInput {

    @NotBlank(message = "Employee name cannot be blank")
    private String name;

    @Positive(message = "Salary must be greater than zero")
    @NotNull(message = "Salary cannot be null")
    private Integer salary;

    @Min(value = 16, message = "Employee age must be at least 16")
    @Max(value = 75, message = "Employee age must be at most 75")
    @NotNull(message = "Age cannot be null")
    private Integer age;

    @NotBlank(message = "Employee title cannot be blank")
    private String title;
}

