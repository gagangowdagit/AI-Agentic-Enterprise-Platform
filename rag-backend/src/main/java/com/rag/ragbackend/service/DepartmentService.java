package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CreateDepartmentRequest;
import com.rag.ragbackend.dto.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    List<DepartmentResponse> getDepartments();

    DepartmentResponse createDepartment(CreateDepartmentRequest request);
}