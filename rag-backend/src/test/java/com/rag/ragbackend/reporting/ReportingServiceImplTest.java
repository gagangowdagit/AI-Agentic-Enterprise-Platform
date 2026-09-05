package com.rag.ragbackend.reporting;

import com.rag.ragbackend.analytics.ProjectAnalytics;
import com.rag.ragbackend.analytics.ProjectAnalyticsService;
import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import com.rag.ragbackend.service.TaskService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportingServiceImplTest {

    @Test
    void mapsProjectAnalyticsIntoStructuredReport() {
        ProjectAnalyticsService analyticsService = mock(ProjectAnalyticsService.class);
        Task overdueTask = new Task("task-2", "project-1", "Pending task", null, "todo", "high");
        Project project = new Project("project-1", "Alpha", "active");
        ProjectAnalytics analytics = new ProjectAnalytics(
                project,
                4,
                2,
                2,
                50.0,
                List.of(overdueTask),
                true);
        when(analyticsService.analyzeProject("project-1")).thenReturn(analytics);
        ReportingService reportingService = new ReportingServiceImpl(analyticsService);

        ProjectReport report = reportingService.generateProjectReport("project-1");

        assertEquals("project-1", report.projectId());
        assertEquals("Alpha", report.projectName());
        assertEquals("active", report.projectStatus());
        assertEquals(4, report.totalTasks());
        assertEquals(2, report.completedTasks());
        assertEquals(2, report.pendingTasks());
        assertEquals(50.0, report.completionPercentage());
        assertEquals(List.of(overdueTask), report.overdueTasks());
        assertTrue(report.overdueTrackingAvailable());
        verify(analyticsService).analyzeProject("project-1");
    }

    @Test
    void preservesUnavailableOverdueTracking() {
        ProjectAnalyticsService analyticsService = mock(ProjectAnalyticsService.class);
        Project project = new Project("project-2", "Beta", "planning");
        when(analyticsService.analyzeProject("project-2")).thenReturn(
                new ProjectAnalytics(project, 0, 0, 0, 0.0, List.of(), false));
        ReportingService reportingService = new ReportingServiceImpl(analyticsService);

        ProjectReport report = reportingService.generateProjectReport("project-2");

        assertEquals("planning", report.projectStatus());
        assertEquals(0, report.totalTasks());
        assertTrue(report.overdueTasks().isEmpty());
        assertFalse(report.overdueTrackingAvailable());
    }

    @Test
    void generatesAiSummaryFromMetricsAndProjectTasks() {
        ProjectAnalyticsService analyticsService = mock(ProjectAnalyticsService.class);
        TaskService taskService = mock(TaskService.class);
        LlmService llmService = mock(LlmService.class);
        Project project = new Project("project-3", "Gamma", "active");
        Task task = new Task("task-3", "project-3", "Review release", null, "todo", "high");
        ProjectAnalytics analytics = new ProjectAnalytics(project, 3, 1, 2, 33.33, List.of(), false);
        String request = "Provide a concise project report covering project health, progress, key issues, and areas needing attention.";
        LlmResponse response = new LlmResponse(request, "Progress is limited; review the pending release work.", List.of());
        when(analyticsService.analyzeProject("project-3")).thenReturn(analytics);
        when(taskService.getTasksByProjectId("project-3")).thenReturn(List.of(task));
        when(llmService.generateAnswer(org.mockito.ArgumentMatchers.eq(request), org.mockito.ArgumentMatchers.eq(List.of()), org.mockito.ArgumentMatchers.any()))
                .thenReturn(response);
        ReportingService reportingService = new ReportingServiceImpl(analyticsService, taskService, llmService);

        AiProjectSummary summary = reportingService.generateAiProjectSummary("project-3");

        assertEquals("Progress is limited; review the pending release work.", summary.summary());
        assertEquals(3, summary.report().totalTasks());
        assertEquals(2, summary.report().pendingTasks());
        org.mockito.ArgumentCaptor<Object> contextCaptor = org.mockito.ArgumentCaptor.forClass(Object.class);
        verify(llmService).generateAnswer(org.mockito.ArgumentMatchers.eq(request), org.mockito.ArgumentMatchers.eq(List.of()), contextCaptor.capture());
        Map<?, ?> context = (Map<?, ?>) contextCaptor.getValue();
        assertEquals("project-3", context.get("project"));
        assertEquals("Gamma", context.get("projectName"));
        assertEquals("active", context.get("projectStatus"));
        assertEquals(summary.report(), context.get("metrics"));
        assertEquals(List.of(task), context.get("tasks"));
        verify(taskService).getTasksByProjectId("project-3");
    }
}