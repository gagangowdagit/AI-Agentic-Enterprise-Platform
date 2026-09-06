package com.rag.ragbackend.dto;

import com.rag.ragbackend.entity.Department;
import com.rag.ragbackend.entity.Employee;

public record EmployeeTeamResponse(
        Integer id,
        String firstName,
        String lastName,
        String email,
        String role,
        DepartmentInfo department) {

    public static EmployeeTeamResponse from(Employee employee) {
        Department department = employee.getDepartment();
        DepartmentInfo departmentInfo = department == null
                ? null
                : new DepartmentInfo(department.getId(), department.getName());

        return new EmployeeTeamResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getRole(),
                departmentInfo);
    }

    public record DepartmentInfo(Integer id, String name) {
    }
}