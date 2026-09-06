package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.EmployeeTeamResponse;
import com.rag.ragbackend.entity.Department;
import com.rag.ragbackend.entity.Employee;
import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.exception.EmployeeNotFoundException;
import com.rag.ragbackend.exception.ProjectNotFoundException;
import com.rag.ragbackend.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TeamServiceImplTest {

    @Test
    void returnsEmployeesForTheSelectedProjectWithDepartmentDetails() {
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        ProjectService projectService = mock(ProjectService.class);
        Project project = new Project(7L, "Alpha", "active");
        Department department = new Department("Engineering", "Builds products");
        Employee employee = new Employee("Ada", "Lovelace", "ada@example.com", "Developer", department, project);

        when(projectService.getProject("7")).thenReturn(project);
        when(employeeRepository.findByProjectId(project.getId())).thenReturn(List.of(employee));

        List<EmployeeTeamResponse> result = new TeamServiceImpl(employeeRepository, projectService)
                .getTeamMembers("7");

        assertEquals(1, result.size());
        assertEquals("Ada", result.get(0).firstName());
        assertEquals("Lovelace", result.get(0).lastName());
        assertEquals("ada@example.com", result.get(0).email());
        assertEquals("Developer", result.get(0).role());
        assertEquals("Engineering", result.get(0).department().name());
        verify(employeeRepository).findByProjectId(project.getId());
    }

    @Test
    void assignsAnExistingEmployeeToTheSelectedProject() {
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        ProjectService projectService = mock(ProjectService.class);
        Project project = new Project(7L, "Alpha", "active");
        Employee employee = new Employee("Ada", "Lovelace", "ada@example.com", "Developer", null, null);

        when(projectService.getProject("7")).thenReturn(project);
        when(employeeRepository.findById(4)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        new TeamServiceImpl(employeeRepository, projectService).addTeamMember("7", 4);

        assertEquals(project, employee.getProject());
        verify(employeeRepository).save(employee);
    }

    @Test
    void removesAnEmployeeFromTheSelectedProject() {
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        ProjectService projectService = mock(ProjectService.class);
        Project project = new Project(7L, "Alpha", "active");
        Employee employee = new Employee("Ada", "Lovelace", "ada@example.com", "Developer", null, project);

        when(projectService.getProject("7")).thenReturn(project);
        when(employeeRepository.findById(4)).thenReturn(Optional.of(employee));

        new TeamServiceImpl(employeeRepository, projectService).removeTeamMember("7", 4);

        assertEquals(null, employee.getProject());
        verify(employeeRepository).save(employee);
    }

    @Test
    void rejectsMissingEmployeeBeforeChangingTheRelationship() {
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        ProjectService projectService = mock(ProjectService.class);
        when(projectService.getProject("7")).thenReturn(new Project(7L, "Alpha", "active"));
        when(employeeRepository.findById(4)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
                () -> new TeamServiceImpl(employeeRepository, projectService).addTeamMember("7", 4));

        verify(employeeRepository, never()).save(org.mockito.ArgumentMatchers.any(Employee.class));
    }

    @Test
    void rejectsMissingProjectBeforeLookingUpTheEmployee() {
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        ProjectService projectService = mock(ProjectService.class);
        when(projectService.getProject("7")).thenThrow(new ProjectNotFoundException("7"));

        assertThrows(ProjectNotFoundException.class,
                () -> new TeamServiceImpl(employeeRepository, projectService).addTeamMember("7", 4));

        verify(employeeRepository, never()).findById(4);
    }
}