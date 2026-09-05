package com.rag.ragbackend.controller;

import com.rag.ragbackend.analytics.ProjectAnalytics;
import com.rag.ragbackend.analytics.ProjectAnalyticsService;
import com.rag.ragbackend.analytics.ProjectRiskAnalysis;
import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectAnalyticsControllerTest {

    @Test
    void returnsStructuredProjectRiskAnalysis() throws Exception {
        ProjectAnalyticsService service = mock(ProjectAnalyticsService.class);
        ProjectAnalyticsController controller = new ProjectAnalyticsController(service);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        ProjectAnalytics metrics = new ProjectAnalytics(
                new Project("project-1", "Alpha", "active"), 2, 1, 1, 50.0, List.of(), false);
        ProjectRiskAnalysis analysis = new ProjectRiskAnalysis(
                metrics,
                List.of("Pending work remains in the project."),
                List.of("Fix deployment"),
                List.of("Fix deployment"),
                List.of("Prioritize pending work."),
                null);
        when(service.analyzeRisks("project-1")).thenReturn(analysis);

        mockMvc.perform(post("/api/v1/analytics/projects/project-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.metrics.totalTasks").value(2))
                .andExpect(jsonPath("$.data.risks[0]").value("Pending work remains in the project."))
                .andExpect(jsonPath("$.data.recommendations[0]").value("Prioritize pending work."));

        verify(service).analyzeRisks("project-1");
    }
}