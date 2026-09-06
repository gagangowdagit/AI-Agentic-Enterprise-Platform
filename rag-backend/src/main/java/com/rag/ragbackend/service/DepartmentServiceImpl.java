package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CreateDepartmentRequest;
import com.rag.ragbackend.dto.DepartmentResponse;
import com.rag.ragbackend.entity.Department;
import com.rag.ragbackend.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<DepartmentResponse> getDepartments() {
        return departmentRepository.findAll().stream()
                .map(DepartmentResponse::from)
                .toList();
    }

    @Override
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {
        Department department = new Department(request.name().trim(), request.description());
        return DepartmentResponse.from(departmentRepository.save(department));
    }
}