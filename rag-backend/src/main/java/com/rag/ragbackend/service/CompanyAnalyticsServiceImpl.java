package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.CompanyAnalyticsResponse;
import com.rag.ragbackend.repository.DepartmentRepository;
import com.rag.ragbackend.repository.EmployeeRepository;
import com.rag.ragbackend.repository.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class CompanyAnalyticsServiceImpl implements CompanyAnalyticsService {

    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public CompanyAnalyticsServiceImpl(ProjectRepository projectRepository,
                                       DepartmentRepository departmentRepository,
                                       EmployeeRepository employeeRepository) {
        this.projectRepository = projectRepository;
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public CompanyAnalyticsResponse getOverview() {
        long totalProjects = projectRepository.count();
        long completedProjects = projectRepository.countByStatusIgnoreCase("completed");
        long inProgressProjects = projectRepository.countByStatusIgnoreCase("active")
                + projectRepository.countByStatusIgnoreCase("in progress")
                + projectRepository.countByStatusIgnoreCase("in_progress")
                + projectRepository.countByStatusIgnoreCase("progressed");
        long pendingProjects = Math.max(0, totalProjects - completedProjects - inProgressProjects);

        return new CompanyAnalyticsResponse(
                totalProjects,
                departmentRepository.count(),
                employeeRepository.count(),
                completedProjects,
                inProgressProjects,
                pendingProjects);
    }
}