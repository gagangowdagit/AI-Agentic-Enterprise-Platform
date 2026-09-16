package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CreateEmployeeRequest;
import com.rag.ragbackend.dto.EmployeeTeamResponse;
import com.rag.ragbackend.entity.Department;
import com.rag.ragbackend.entity.Employee;
import com.rag.ragbackend.repository.DepartmentRepository;
import com.rag.ragbackend.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Stream;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<EmployeeTeamResponse> getEmployees(Integer departmentId) {
        Stream<Employee> employees = departmentId == null
                ? employeeRepository.findAll().stream()
                : employeeRepository.findByDepartmentId(departmentId).stream();

        return employees.map(EmployeeTeamResponse::from).toList();
    }

    @Override
    public EmployeeTeamResponse createEmployee(CreateEmployeeRequest request) {
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Department not found: " + request.departmentId()));

        Employee employee = new Employee(
                request.firstName().trim(),
                request.lastName().trim(),
                request.email().trim(),
                request.role().trim(),
                department,
                null);

        return EmployeeTeamResponse.from(employeeRepository.save(employee));
    }
}
