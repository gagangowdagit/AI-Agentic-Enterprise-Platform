package com.rag.ragbackend.controller;

import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.entity.ProjectPriority;
import com.rag.ragbackend.exception.GlobalExceptionHandler;
import com.rag.ragbackend.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest {

    private final ProjectService projectService = mock(ProjectService.class);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProjectController(projectService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void createsProjectWithDescriptionDatesAndPriority() throws Exception {
        Project project = new Project(1L, "Alpha", "Delivery plan", "active",
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), ProjectPriority.HIGH);
        when(projectService.createProject(any(Project.class))).thenReturn(project);

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Alpha",
                                  "description": "Delivery plan",
                                  "status": "active",
                                  "startDate": "2026-09-01",
                                  "endDate": "2026-09-30",
                                  "priority": "HIGH"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Delivery plan"))
                .andExpect(jsonPath("$.startDate").value("2026-09-01"))
                .andExpect(jsonPath("$.endDate").value("2026-09-30"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(projectService).createProject(any(Project.class));
    }

    @Test
    void retrievesProjectAndListsAllFields() throws Exception {
        Project project = new Project(1L, "Alpha", null, "active", null, null, ProjectPriority.MEDIUM);
        when(projectService.getProject("project-1")).thenReturn(project);
        when(projectService.getAllProjects()).thenReturn(List.of(project));

        mockMvc.perform(get("/api/v1/projects/project-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alpha"))
                .andExpect(jsonPath("$[0].description").doesNotExist());
    }

    @Test
    void retrievesProjectDetailsWithAllFields() throws Exception {
        Project project = new Project(42L, "Alpha", "Delivery plan", "active",
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), ProjectPriority.HIGH);
        when(projectService.getProjectDetails("42")).thenReturn(project);

        mockMvc.perform(get("/api/v1/projects/42/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Alpha"))
                .andExpect(jsonPath("$.description").value("Delivery plan"))
                .andExpect(jsonPath("$.status").value("active"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.startDate").value("2026-09-01"))
                .andExpect(jsonPath("$.endDate").value("2026-09-30"));

        verify(projectService).getProjectDetails("42");
    }

    @Test
    void updatesAllProjectFields() throws Exception {
        Project updated = new Project(1L, "Beta", "Updated scope", "paused",
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), ProjectPriority.LOW);
        when(projectService.updateProject(any(String.class), any(Project.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/projects/project-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Beta",
                                  "description": "Updated scope",
                                  "status": "paused",
                                  "startDate": "2026-10-01",
                                  "endDate": "2026-10-31",
                                  "priority": "LOW"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Beta"))
                .andExpect(jsonPath("$.description").value("Updated scope"))
                .andExpect(jsonPath("$.priority").value("LOW"));

        verify(projectService).updateProject(any(String.class), any(Project.class));
    }

    @Test
    void rejectsMissingOrInvalidPriority() throws Exception {
        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alpha\",\"status\":\"active\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alpha\",\"status\":\"active\",\"priority\":\"URGENT\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletesProject() throws Exception {
        mockMvc.perform(delete("/api/v1/projects/project-1"))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProject("project-1");
    }
}
