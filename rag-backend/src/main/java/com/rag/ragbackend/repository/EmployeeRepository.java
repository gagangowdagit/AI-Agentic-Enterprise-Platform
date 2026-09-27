package com.rag.ragbackend.repository;

import com.rag.ragbackend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByEmail(String email);

    List<Employee> findByProjectId(Integer projectId);

    List<Employee> findByDepartmentId(Integer departmentId);

    long countByDepartmentId(Integer departmentId);
}