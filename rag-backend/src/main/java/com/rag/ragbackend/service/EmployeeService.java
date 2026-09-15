package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CreateEmployeeRequest;
import com.rag.ragbackend.dto.EmployeeTeamResponse;

public interface EmployeeService {

    EmployeeTeamResponse createEmployee(CreateEmployeeRequest request);
}
