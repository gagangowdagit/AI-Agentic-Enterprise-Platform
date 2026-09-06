package com.rag.ragbackend.repository;

import com.rag.ragbackend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    List<Employee> findByProjectId(Integer projectId);
}