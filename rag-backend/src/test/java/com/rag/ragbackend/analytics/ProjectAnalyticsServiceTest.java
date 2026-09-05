package com.rag.ragbackend.analytics;

import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import com.rag.ragbackend.service.ProjectService;
import com.rag.ragbackend.service.TaskService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectAnalyticsServiceTest {

    @Test
    void calculatesProjectTaskMetrics() {
        ProjectService projectService = mock(ProjectService.class);
        TaskService taskService = mock(TaskService.class);
        ProjectAnalyticsService service = new ProjectAnalyticsServiceImpl(projectService, taskService);
        Project project = new Project("project-1", "Alpha", "active");
        List<Task> tasks = List.of(
                new Task("task-1", "project-1", "Ship release", null, "completed", "high"),
                new Task("task-2", "project-1", "Write docs", null, "done", "medium"),
                new Task("task-3", "project-1", "Fix deployment", null, "todo", "high"),
                new Task("task-4", "project-1", "Review logs", null, "in_progress", "low"));
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(taskService.getTasksByProjectId("project-1")).thenReturn(tasks);

        ProjectAnalytics analytics = service.analyzeProject("project-1");

        assertEquals(project, analytics.project());
        assertEquals(4, analytics.totalTasks());
        assertEquals(2, analytics.completedTasks());
        assertEquals(2, analytics.pendingTasks());
        assertEquals(50.0, analytics.completionPercentage());
        assertEquals(List.of(), analytics.overdueTasks());
        assertEquals(false, analytics.overdueTrackingAvailable());
        verify(taskService).getTasksByProjectId("project-1");
    }

    @Test
    void returnsZeroPercentageForProjectWithoutTasks() {
        ProjectService projectService = mock(ProjectService.class);
        TaskService taskService = mock(TaskService.class);
        ProjectAnalyticsService service = new ProjectAnalyticsServiceImpl(projectService, taskService);
        Project project = new Project("project-1", "Alpha", "active");
        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(taskService.getTasksByProjectId("project-1")).thenReturn(List.of());

        ProjectAnalytics analytics = service.analyzeProject("project-1");

        assertEquals(0, analytics.totalTasks());
        assertEquals(0, analytics.completedTasks());
        assertEquals(0, analytics.pendingTasks());
        assertEquals(0.0, analytics.completionPercentage());
    }

    @Test
    void rejectsMissingOrUnknownProject() {
        ProjectService projectService = mock(ProjectService.class);
        TaskService taskService = mock(TaskService.class);
        ProjectAnalyticsService service = new ProjectAnalyticsServiceImpl(projectService, taskService);
        when(projectService.getAllProjects()).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> service.analyzeProject(" "));
        assertThrows(IllegalArgumentException.class, () -> service.analyzeProject("project-1"));
    }

    @Test
    void analyzesRisksAndGeneratesAiContext() {
        ProjectService projectService = mock(ProjectService.class);
        TaskService taskService = mock(TaskService.class);
        LlmService llmService = mock(LlmService.class);
        ProjectAnalyticsService service = new ProjectAnalyticsServiceImpl(projectService, taskService, llmService);
        Project project = new Project("project-1", "Alpha", "paused");
        Task highPriorityTask = new Task("task-1", "project-1", "Fix deployment", null, "todo", "high");
        Task completedTask = new Task("task-2", "project-1", "Ship release", null, "completed", "medium");
        LlmResponse aiResponse = new LlmResponse("risk request", "Review deployment risk.", List.of());

        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(taskService.getTasksByProjectId("project-1")).thenReturn(List.of(highPriorityTask, completedTask));
        when(llmService.generateAnswer(
            org.mockito.ArgumentMatchers.eq("Analyze project risks, bottlenecks, priorities, and recommended next actions."),
            org.mockito.ArgumentMatchers.eq(List.of()),
                org.mockito.ArgumentMatchers.any())).thenReturn(aiResponse);

        ProjectRiskAnalysis analysis = service.analyzeRisks("project-1");

        assertEquals(2, analysis.metrics().totalTasks());
        assertEquals(List.of("Pending work remains in the project.", "Project status is not active."), analysis.risks());
        assertEquals(List.of("Fix deployment"), analysis.bottlenecks());
        assertEquals(List.of("Fix deployment"), analysis.priorities());
        assertEquals(List.of("Prioritize and progress the pending tasks."), analysis.recommendations());
        assertEquals(aiResponse, analysis.aiAnalysis());
    }
}