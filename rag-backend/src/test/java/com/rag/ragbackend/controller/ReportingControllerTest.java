package com.rag.ragbackend.controller;

import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.exception.GlobalExceptionHandler;
import com.rag.ragbackend.reporting.AiProjectSummary;
import com.rag.ragbackend.reporting.ProjectReport;
import com.rag.ragbackend.reporting.ReportingService;
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

class ReportingControllerTest {

    @Test
    void returnsStructuredProjectMetricsAndAiSummary() throws Exception {
        ReportingService service = mock(ReportingService.class);
        Task overdueTask = new Task("task-1", "project-1", "Review release", null, "todo", "high");
        ProjectReport report = new ProjectReport(
                "project-1", "Alpha", "active", 4, 2, 2, 50.0, List.of(overdueTask), true);
        when(service.generateAiProjectSummary("project-1"))
                .thenReturn(new AiProjectSummary(report, "Progress is halfway complete; review the pending release."));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ReportingController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/reports/projects/project-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.report.projectId").value("project-1"))
                .andExpect(jsonPath("$.data.report.totalTasks").value(4))
                .andExpect(jsonPath("$.data.report.completedTasks").value(2))
                .andExpect(jsonPath("$.data.report.pendingTasks").value(2))
                .andExpect(jsonPath("$.data.report.completionPercentage").value(50.0))
                .andExpect(jsonPath("$.data.report.overdueTrackingAvailable").value(true))
                .andExpect(jsonPath("$.data.summary").value("Progress is halfway complete; review the pending release."));

        verify(service).generateAiProjectSummary("project-1");
    }
}