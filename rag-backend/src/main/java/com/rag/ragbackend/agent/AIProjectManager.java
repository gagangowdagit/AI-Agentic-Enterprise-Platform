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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AIProjectManager {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final AgentCoordinator agentCoordinator;
    private final RagService ragService;
    private final LlmService llmService;
    private final NotificationService notificationService;

    @Autowired
    public AIProjectManager(
            ProjectService projectService,
            TaskService taskService,
            AgentCoordinator agentCoordinator,
            RagService ragService,
            LlmService llmService) {
        this(projectService, taskService, agentCoordinator, ragService, llmService, null);
    }

    public AIProjectManager(
            ProjectService projectService,
            TaskService taskService,
            AgentCoordinator agentCoordinator,
            RagService ragService,
            LlmService llmService,
            NotificationService notificationService) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.agentCoordinator = agentCoordinator;
        this.ragService = ragService;
        this.llmService = llmService;
        this.notificationService = notificationService;
    }

    public LlmResponse manageProject(String projectId, String userRequest) {
        requireValue(projectId, "Project ID");
        requireValue(userRequest, "User request");

        Project project = findProject(projectId);
        List<Task> tasks = taskService.getTasksByProjectId(projectId);
        SpecializedAgent selectedAgent = agentCoordinator.selectAgent(userRequest);
        List<DocumentChunk> relevantChunks = ragService.retrieveRelevantChunks(projectId, userRequest, 5);

        Map<String, Object> projectContext = new HashMap<>();
        projectContext.put("project", project);
        projectContext.put("tasks", tasks);
        projectContext.put("agent", selectedAgent.getName());
        projectContext.put("agentCapability", selectedAgent.getCapabilityDescription());

        return llmService.generateAnswer(userRequest, relevantChunks, projectContext);
    }

    public LlmResponse analyzeProject(String projectId) {
        requireValue(projectId, "Project ID");

        Project project = findProject(projectId);
        List<Task> tasks = taskService.getTasksByProjectId(projectId);
        String analysisRequest = "Analyze the current project state and provide a concise project-management summary.";
        SpecializedAgent selectedAgent = agentCoordinator.selectAgent(analysisRequest);

        List<Task> completedTasks = tasks.stream()
                .filter(this::isCompleted)
                .toList();
        List<Task> pendingTasks = tasks.stream()
                .filter(task -> !isCompleted(task))
                .toList();

        Map<String, Object> analysisContext = new HashMap<>();
        analysisContext.put("project", project);
        analysisContext.put("overallProjectStatus", project.getStatus());
        analysisContext.put("completedTasks", completedTasks);
        analysisContext.put("pendingTasks", pendingTasks);
        analysisContext.put("overdueTasks", List.of());
        analysisContext.put("overdueTrackingAvailable", false);
        analysisContext.put("agent", selectedAgent.getName());
        analysisContext.put("agentCapability", selectedAgent.getCapabilityDescription());

        return llmService.generateAnswer(analysisRequest, List.of(), analysisContext);
    }

    public LlmResponse recommendProject(String projectId) {
        requireValue(projectId, "Project ID");

        LlmResponse stateAnalysis = analyzeProject(projectId);
        Project project = findProject(projectId);
        List<Task> tasks = taskService.getTasksByProjectId(projectId);
        String recommendationRequest = "Provide practical project recommendations based on the current state.";
        SpecializedAgent selectedAgent = agentCoordinator.selectAgent(recommendationRequest);

        List<Task> pendingTasks = tasks.stream()
                .filter(task -> !isCompleted(task))
                .toList();
        List<String> potentialIssues = new java.util.ArrayList<>();
        if (pendingTasks.isEmpty()) {
            potentialIssues.add("No pending tasks are currently recorded.");
        } else {
            potentialIssues.add("Pending tasks require follow-up.");
        }
        if (project.getStatus() == null || !project.getStatus().equalsIgnoreCase("active")) {
            potentialIssues.add("The project status is not active.");
        }

        List<String> priorities = pendingTasks.stream()
                .map(Task::getTitle)
                .filter(title -> title != null && !title.isBlank())
                .toList();
        List<String> suggestedNextActions = priorities.isEmpty()
                ? List.of("Review the project status and define the next task.")
                : List.of("Review and progress the pending tasks.");

        Map<String, Object> recommendationContext = new HashMap<>();
        recommendationContext.put("project", project);
        recommendationContext.put("tasks", tasks);
        recommendationContext.put("stateAnalysis", stateAnalysis);
        recommendationContext.put("potentialIssues", potentialIssues);
        recommendationContext.put("priorities", priorities);
        recommendationContext.put("suggestedNextActions", suggestedNextActions);
        recommendationContext.put("agent", selectedAgent.getName());
        recommendationContext.put("agentCapability", selectedAgent.getCapabilityDescription());

        LlmResponse response = llmService.generateAnswer(recommendationRequest, List.of(), recommendationContext);
        notifyProjectRisks(tasks, potentialIssues);
        return response;
    }

    private boolean isCompleted(Task task) {
        if (task.getStatus() == null) {
            return false;
        }
        String status = task.getStatus().toLowerCase(Locale.ROOT).trim();
        return status.equals("completed") || status.equals("done") || status.equals("closed");
    }

    private Project findProject(String projectId) {
        return projectService.getAllProjects().stream()
            .filter(project -> (project.getId() != null && projectId.equals(project.getId().toString()))
                || projectId.equals(project.getLegacyId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
    }

    private void requireValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    private void notifyProjectRisks(List<Task> tasks, List<String> potentialIssues) {
        if (notificationService == null || potentialIssues.isEmpty()) {
            return;
        }
        tasks.stream()
            .map(task -> task.getAssignedUserId())
                .filter(java.util.Objects::nonNull)
                .distinct()
                .forEach(userId -> {
                    try {
                        notificationService.createNotification(
                                userId,
                                "PROJECT_RISK",
                                "Project risk detected",
                                String.join(" ", potentialIssues));
                    } catch (RuntimeException ignored) {
                        // Notifications must not make project analysis fail.
                    }
                });
    }
}