package com.rag.ragbackend.dto;

import com.rag.ragbackend.entity.Employee;

public record EmployeeAnalyticsResponse(
        Integer id,
        String firstName,
        String lastName,
        String email,
        String role,
        String departmentName) {

    public static EmployeeAnalyticsResponse from(Employee employee) {
        String departmentName = employee.getDepartment() == null ? "Unassigned" : employee.getDepartment().getName();
        return new EmployeeAnalyticsResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getRole(),
                departmentName);
    }
}
