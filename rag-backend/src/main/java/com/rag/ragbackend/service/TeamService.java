package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.EmployeeTeamResponse;

import java.util.List;

public interface TeamService {

    List<EmployeeTeamResponse> getTeamMembers(String projectId);

    List<EmployeeTeamResponse> getAvailableEmployees(String projectId);

    EmployeeTeamResponse addTeamMember(String projectId, Integer employeeId);

    void removeTeamMember(String projectId, Integer employeeId);
}