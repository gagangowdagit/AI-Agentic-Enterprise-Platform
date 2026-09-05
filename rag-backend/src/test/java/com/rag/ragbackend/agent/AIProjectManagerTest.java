package com.rag.ragbackend.agent;

import com.rag.ragbackend.entity.DocumentChunk;
import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import com.rag.ragbackend.processing.RagService;
import com.rag.ragbackend.service.ProjectService;
import com.rag.ragbackend.service.NotificationService;
import com.rag.ragbackend.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AIProjectManagerTest {

    @Test
    void gathersProjectTasksAndRagContextForLlmResponse() {
        ProjectService projectService = mock(ProjectService.class);
        TaskService taskService = mock(TaskService.class);
        AgentCoordinator agentCoordinator = mock(AgentCoordinator.class);
        RagService ragService = mock(RagService.class);
        LlmService llmService = mock(LlmService.class);
        SpecializedAgent selectedAgent = mock(SpecializedAgent.class);
        AIProjectManager manager = new AIProjectManager(projectService, taskService, agentCoordinator, ragService, llmService);
        Project project = new Project("project-1", "Alpha", "active");
        Task task = new Task("task-1", "project-1", "Prepare report", null, "todo", "high");
        List<DocumentChunk> chunks = List.of(new DocumentChunk());
        LlmResponse expected = new LlmResponse("Summarize project progress", "Progress is on track.", chunks);

        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(taskService.getTasksByProjectId("project-1")).thenReturn(List.of(task));
        when(agentCoordinator.selectAgent("Summarize project progress")).thenReturn(selectedAgent);
        when(selectedAgent.getName()).thenReturn("general-agent");
        when(selectedAgent.getCapabilityDescription()).thenReturn("Project management");
        when(ragService.retrieveRelevantChunks("project-1", "Summarize project progress", 5)).thenReturn(chunks);
        when(llmService.generateAnswer(eq("Summarize project progress"), eq(chunks), any())).thenReturn(expected);

        LlmResponse actual = manager.manageProject("project-1", "Summarize project progress");

        assertEquals(expected, actual);
        verify(taskService).getTasksByProjectId("project-1");
        verify(agentCoordinator).selectAgent("Summarize project progress");
        verify(llmService).generateAnswer(eq("Summarize project progress"), eq(chunks), any());
    }

    @Test
    void rejectsMissingProjectOrUnknownProject() {
        ProjectService projectService = mock(ProjectService.class);
        AIProjectManager manager = new AIProjectManager(
                projectService,
                mock(TaskService.class),
                mock(AgentCoordinator.class),
                mock(RagService.class),
                mock(LlmService.class));
        when(projectService.getAllProjects()).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> manager.manageProject(" ", "status"));
        assertThrows(IllegalArgumentException.class, () -> manager.manageProject("project-1", "status"));
    }

    @Test
    void analyzesProjectStateAndCategorizesTasks() {
        ProjectService projectService = mock(ProjectService.class);
        TaskService taskService = mock(TaskService.class);
        AgentCoordinator agentCoordinator = mock(AgentCoordinator.class);
        RagService ragService = mock(RagService.class);
        LlmService llmService = mock(LlmService.class);
        SpecializedAgent selectedAgent = mock(SpecializedAgent.class);
        AIProjectManager manager = new AIProjectManager(projectService, taskService, agentCoordinator, ragService, llmService);
        Project project = new Project("project-1", "Alpha", "active");
        Task completedTask = new Task("task-1", "project-1", "Ship release", null, "completed", "high");
        Task pendingTask = new Task("task-2", "project-1", "Write documentation", null, "in_progress", "medium");
        LlmResponse expected = new LlmResponse(
                "Analyze the current project state and provide a concise project-management summary.",
                "One task is complete and one remains pending.",
                List.of());

        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(taskService.getTasksByProjectId("project-1")).thenReturn(List.of(completedTask, pendingTask));
        when(agentCoordinator.selectAgent(expected.query())).thenReturn(selectedAgent);
        when(selectedAgent.getName()).thenReturn("general-agent");
        when(selectedAgent.getCapabilityDescription()).thenReturn("Project management");
        when(llmService.generateAnswer(eq(expected.query()), eq(List.of()), any())).thenReturn(expected);

        LlmResponse actual = manager.analyzeProject("project-1");

        assertEquals(expected, actual);
        ArgumentCaptor<Object> contextCaptor = ArgumentCaptor.forClass(Object.class);
        verify(llmService).generateAnswer(eq(expected.query()), eq(List.of()), contextCaptor.capture());
        Map<?, ?> context = (Map<?, ?>) contextCaptor.getValue();
        assertEquals(List.of(completedTask), context.get("completedTasks"));
        assertEquals(List.of(pendingTask), context.get("pendingTasks"));
        assertEquals(List.of(), context.get("overdueTasks"));
        assertEquals(false, context.get("overdueTrackingAvailable"));
        assertEquals("active", context.get("overallProjectStatus"));
    }

    @Test
    void analyzesStateBeforeGeneratingReadOnlyRecommendations() {
        ProjectService projectService = mock(ProjectService.class);
        TaskService taskService = mock(TaskService.class);
        AgentCoordinator agentCoordinator = mock(AgentCoordinator.class);
        RagService ragService = mock(RagService.class);
        LlmService llmService = mock(LlmService.class);
        SpecializedAgent selectedAgent = mock(SpecializedAgent.class);
        AIProjectManager manager = new AIProjectManager(projectService, taskService, agentCoordinator, ragService, llmService);
        Project project = new Project("project-1", "Alpha", "active");
        Task completedTask = new Task("task-1", "project-1", "Ship release", null, "done", "high");
        Task pendingTask = new Task("task-2", "project-1", "Fix deployment", null, "todo", "high");
        String analysisRequest = "Analyze the current project state and provide a concise project-management summary.";
        String recommendationRequest = "Provide practical project recommendations based on the current state.";
        LlmResponse stateAnalysis = new LlmResponse(analysisRequest, "One task remains pending.", List.of());
        LlmResponse recommendations = new LlmResponse(recommendationRequest, "Prioritize deployment work.", List.of());

        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(taskService.getTasksByProjectId("project-1")).thenReturn(List.of(completedTask, pendingTask));
        when(agentCoordinator.selectAgent(any(String.class))).thenReturn(selectedAgent);
        when(selectedAgent.getName()).thenReturn("general-agent");
        when(selectedAgent.getCapabilityDescription()).thenReturn("Project management");
        when(llmService.generateAnswer(eq(analysisRequest), eq(List.of()), any())).thenReturn(stateAnalysis);
        when(llmService.generateAnswer(eq(recommendationRequest), eq(List.of()), any())).thenReturn(recommendations);

        LlmResponse actual = manager.recommendProject("project-1");

        assertEquals(recommendations, actual);
        verify(llmService).generateAnswer(eq(analysisRequest), eq(List.of()), any());
        ArgumentCaptor<Object> recommendationContextCaptor = ArgumentCaptor.forClass(Object.class);
        verify(llmService).generateAnswer(eq(recommendationRequest), eq(List.of()), recommendationContextCaptor.capture());
        Map<?, ?> context = (Map<?, ?>) recommendationContextCaptor.getValue();
        assertEquals(stateAnalysis, context.get("stateAnalysis"));
        assertEquals(List.of("Pending tasks require follow-up."), context.get("potentialIssues"));
        assertEquals(List.of("Fix deployment"), context.get("priorities"));
        assertEquals(List.of("Review and progress the pending tasks."), context.get("suggestedNextActions"));
        verify(taskService, never()).createTask(any(Task.class));
        verify(taskService, never()).updateTask(any(String.class), any(Task.class));
    }

    @Test
    void createsRiskNotificationsForAssignedUsersWhenRecommendationsFindIssues() {
        ProjectService projectService = mock(ProjectService.class);
        TaskService taskService = mock(TaskService.class);
        NotificationService notificationService = mock(NotificationService.class);
        AgentCoordinator agentCoordinator = mock(AgentCoordinator.class);
        RagService ragService = mock(RagService.class);
        LlmService llmService = mock(LlmService.class);
        SpecializedAgent selectedAgent = mock(SpecializedAgent.class);
        AIProjectManager manager = new AIProjectManager(
                projectService, taskService, agentCoordinator, ragService, llmService, notificationService);
        Project project = new Project("project-1", "Alpha", "active");
        Task pendingTask = new Task("task-2", "project-1", 7L, "Fix deployment", null, "todo", "high");
        String analysisRequest = "Analyze the current project state and provide a concise project-management summary.";
        String recommendationRequest = "Provide practical project recommendations based on the current state.";
        LlmResponse stateAnalysis = new LlmResponse(analysisRequest, "One task remains pending.", List.of());
        LlmResponse recommendations = new LlmResponse(recommendationRequest, "Prioritize deployment work.", List.of());

        when(projectService.getAllProjects()).thenReturn(List.of(project));
        when(taskService.getTasksByProjectId("project-1")).thenReturn(List.of(pendingTask));
        when(agentCoordinator.selectAgent(any(String.class))).thenReturn(selectedAgent);
        when(selectedAgent.getName()).thenReturn("general-agent");
        when(selectedAgent.getCapabilityDescription()).thenReturn("Project management");
        when(llmService.generateAnswer(eq(analysisRequest), eq(List.of()), any())).thenReturn(stateAnalysis);
        when(llmService.generateAnswer(eq(recommendationRequest), eq(List.of()), any())).thenReturn(recommendations);

        assertEquals(recommendations, manager.recommendProject("project-1"));

        verify(notificationService).createNotification(
                7L,
                "PROJECT_RISK",
                "Project risk detected",
                "Pending tasks require follow-up.");
    }
}