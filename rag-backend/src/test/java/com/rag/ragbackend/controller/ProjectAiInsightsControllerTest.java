package com.rag.ragbackend.controller;

import com.rag.ragbackend.analytics.ProjectAnalytics;
import com.rag.ragbackend.dto.ProjectAiInsights;
import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.service.ProjectAiInsightsService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectAiInsightsControllerTest {

    @Test
    void returnsStructuredProjectInsights() throws Exception {
        ProjectAiInsightsService service = mock(ProjectAiInsightsService.class);
        ProjectAnalytics metrics = new ProjectAnalytics(
                new Project("project-1", "Alpha", "active"),
                4, 2, 2, 50.0, List.of(), false);
        when(service.getInsights("project-1")).thenReturn(new ProjectAiInsights(
                metrics,
                "HEALTHY",
                List.of("Risk one"),
                List.of("Bottleneck one"),
                List.of("Priority one"),
                List.of("Recommendation one"),
                "AI summary"));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProjectAiInsightsController(service)).build();

        mockMvc.perform(get("/api/v1/projects/project-1/ai-insights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.health").value("HEALTHY"))
                .andExpect(jsonPath("$.data.metrics.totalTasks").value(4))
                .andExpect(jsonPath("$.data.metrics.completionPercentage").value(50.0))
                .andExpect(jsonPath("$.data.risks[0]").value("Risk one"))
                .andExpect(jsonPath("$.data.bottlenecks[0]").value("Bottleneck one"))
                .andExpect(jsonPath("$.data.priorities[0]").value("Priority one"))
                .andExpect(jsonPath("$.data.recommendations[0]").value("Recommendation one"))
                .andExpect(jsonPath("$.data.aiSummary").value("AI summary"));

        verify(service).getInsights("project-1");
    }
}