package com.rag.ragbackend.dto;

import com.rag.ragbackend.entity.Department;

public record DepartmentAnalyticsResponse(
        Integer id,
        String name,
        String description,
        long projectCount,
        long employeeCount) {

    public static DepartmentAnalyticsResponse from(Department department, long projectCount, long employeeCount) {
        return new DepartmentAnalyticsResponse(
                department.getId(),
                department.getName(),
                department.getDescription(),
                projectCount,
                employeeCount);
    }
}
