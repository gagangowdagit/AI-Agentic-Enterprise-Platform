package com.rag.ragbackend.service;

import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.entity.ProjectPriority;
import com.rag.ragbackend.repository.ProjectRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectServiceImplTest {

    @Test
    void updatePersistsEveryProjectField() {
        ProjectRepository repository = mock(ProjectRepository.class);
        Project existing = new Project(1L, "Alpha", "Old scope", "active",
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 15), ProjectPriority.MEDIUM);
        Project update = new Project("ignored", "Beta", "New scope", "paused",
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), ProjectPriority.HIGH);
        when(repository.findById("project-1")).thenReturn(Optional.of(existing));
        when(repository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ProjectServiceImpl service = new ProjectServiceImpl(repository);

        Project result = service.updateProject("project-1", update);

        assertEquals(1L, result.getId());
        assertEquals("Beta", result.getName());
        assertEquals("New scope", result.getDescription());
        assertEquals("paused", result.getStatus());
        assertEquals(LocalDate.of(2026, 10, 1), result.getStartDate());
        assertEquals(LocalDate.of(2026, 10, 31), result.getEndDate());
        assertEquals(ProjectPriority.HIGH, result.getPriority());
        verify(repository).save(existing);
    }

    @Test
    void getsProjectDetailsByProjectId() {
        Project project = new Project(42L, "Alpha", "Delivery plan", "active",
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), ProjectPriority.HIGH);
        ProjectRepository repository = mock(ProjectRepository.class);
        when(repository.findById(42L)).thenReturn(Optional.of(project));
        ProjectServiceImpl service = new ProjectServiceImpl(repository);

        Project result = service.getProjectDetails("42");

        assertEquals(42L, result.getId());
        assertEquals("Alpha", result.getName());
        assertEquals("Delivery plan", result.getDescription());
        assertEquals("active", result.getStatus());
        assertEquals(ProjectPriority.HIGH, result.getPriority());
        assertEquals(LocalDate.of(2026, 9, 1), result.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), result.getEndDate());
        verify(repository).findById(42L);
    }
}
