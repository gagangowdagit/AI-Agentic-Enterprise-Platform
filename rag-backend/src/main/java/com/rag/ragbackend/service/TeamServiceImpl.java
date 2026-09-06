package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.EmployeeTeamResponse;
import com.rag.ragbackend.entity.Employee;
import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.exception.ApiException;
import com.rag.ragbackend.exception.EmployeeNotFoundException;
import com.rag.ragbackend.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    private final EmployeeRepository employeeRepository;
    private final ProjectService projectService;

    public TeamServiceImpl(EmployeeRepository employeeRepository, ProjectService projectService) {
        this.employeeRepository = employeeRepository;
        this.projectService = projectService;
    }

    @Override
    public List<EmployeeTeamResponse> getTeamMembers(String projectId) {
        Project project = projectService.getProject(projectId);
        return employeeRepository.findByProjectId(project.getId())
                .stream()
                .map(EmployeeTeamResponse::from)
                .toList();
    }

    @Override
    public List<EmployeeTeamResponse> getAvailableEmployees(String projectId) {
        projectService.getProject(projectId);
        return employeeRepository.findAll()
                .stream()
                .filter(employee -> employee.getProject() == null)
                .map(EmployeeTeamResponse::from)
                .toList();
    }

    @Override
    public EmployeeTeamResponse addTeamMember(String projectId, Integer employeeId) {
        Project project = projectService.getProject(projectId);
        Employee employee = getEmployee(employeeId);

        if (employee.getProject() != null && !project.getId().equals(employee.getProject().getId())) {
            throw new ApiException("Employee is already assigned to another project", "EMPLOYEE_ALREADY_ASSIGNED");
        }

        employee.setProject(project);
        return EmployeeTeamResponse.from(employeeRepository.save(employee));
    }

    @Override
    public void removeTeamMember(String projectId, Integer employeeId) {
        Project project = projectService.getProject(projectId);
        Employee employee = getEmployee(employeeId);

        if (employee.getProject() == null || !project.getId().equals(employee.getProject().getId())) {
            throw new ApiException("Employee is not assigned to this project", "EMPLOYEE_NOT_IN_PROJECT");
        }

        employee.setProject(null);
        employeeRepository.save(employee);
    }

    private Employee getEmployee(Integer employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }
}