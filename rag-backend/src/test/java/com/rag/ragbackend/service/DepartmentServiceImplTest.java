package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CreateDepartmentRequest;
import com.rag.ragbackend.dto.DepartmentResponse;
import com.rag.ragbackend.entity.Department;
import com.rag.ragbackend.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DepartmentServiceImplTest {

    @Test
    void listsDepartments() {
        DepartmentRepository repository = mock(DepartmentRepository.class);
        Department department = new Department("Engineering", "Product engineering");
        when(repository.findAll()).thenReturn(List.of(department));

        List<DepartmentResponse> result = new DepartmentServiceImpl(repository).getDepartments();

        assertEquals(1, result.size());
        assertEquals("Engineering", result.get(0).name());
    }

    @Test
    void createsDepartmentWithNameAndDescription() {
        DepartmentRepository repository = mock(DepartmentRepository.class);
        Department saved = new Department("Engineering", "Product engineering");
        when(repository.save(org.mockito.ArgumentMatchers.any(Department.class))).thenReturn(saved);

        DepartmentResponse result = new DepartmentServiceImpl(repository)
                .createDepartment(new CreateDepartmentRequest(" Engineering ", "Product engineering"));

        assertEquals("Engineering", result.name());
        assertEquals("Product engineering", result.description());
    }
}