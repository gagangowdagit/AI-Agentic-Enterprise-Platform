package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CreateEmployeeRequest;
import com.rag.ragbackend.dto.EmployeeTeamResponse;

import java.util.List;

public interface EmployeeService {

    List<EmployeeTeamResponse> getEmployees(Integer departmentId);

    EmployeeTeamResponse createEmployee(CreateEmployeeRequest request);
}
