package com.rag.ragbackend.dto;

import com.rag.ragbackend.entity.Department;

public record DepartmentResponse(Integer id, String name, String description) {

    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(department.getId(), department.getName(), department.getDescription());
    }
}