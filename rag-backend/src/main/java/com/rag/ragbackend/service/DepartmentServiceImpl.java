package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CreateDepartmentRequest;
import com.rag.ragbackend.dto.DepartmentResponse;
import com.rag.ragbackend.entity.Department;
import com.rag.ragbackend.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
        String departmentName = request.name().trim();
        if (departmentRepository.existsByNameIgnoreCase(departmentName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Department name already exists");
        }

        Department department = new Department(departmentName, request.description());
        return DepartmentResponse.from(departmentRepository.save(department));
    }
}