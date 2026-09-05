package com.rag.ragbackend.analytics;

import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import com.rag.ragbackend.service.ProjectService;
import com.rag.ragbackend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ProjectAnalyticsServiceImpl implements ProjectAnalyticsService {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final LlmService llmService;

    @Autowired
    public ProjectAnalyticsServiceImpl(ProjectService projectService, TaskService taskService, LlmService llmService) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.llmService = llmService;
    }

    public ProjectAnalyticsServiceImpl(ProjectService projectService, TaskService taskService) {
        this(projectService, taskService, null);
    }

    @Override
    public ProjectAnalytics analyzeProject(String projectId) {
        requireValue(projectId, "Project ID");

        Project project = projectService.getAllProjects().stream()
                .filter(item -> projectId.equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        List<Task> tasks = taskService.getTasksByProjectId(projectId);
        int completedTasks = (int) tasks.stream().filter(this::isCompleted).count();
        int totalTasks = tasks.size();
        int pendingTasks = totalTasks - completedTasks;
        double completionPercentage = totalTasks == 0 ? 0.0 : completedTasks * 100.0 / totalTasks;

        return new ProjectAnalytics(
                project,
                totalTasks,
                completedTasks,
                pendingTasks,
                completionPercentage,
                List.of(),
                false);
    }

            @Override
            public ProjectRiskAnalysis analyzeRisks(String projectId) {
            ProjectAnalytics metrics = analyzeProject(projectId);
            List<Task> tasks = taskService.getTasksByProjectId(projectId);
            List<Task> pendingTasks = tasks.stream().filter(task -> !isCompleted(task)).toList();

            List<String> risks = new java.util.ArrayList<>();
            if (metrics.pendingTasks() > 0) {
                risks.add("Pending work remains in the project.");
            }
            if (metrics.project().getStatus() == null
                || !metrics.project().getStatus().equalsIgnoreCase("active")) {
                risks.add("Project status is not active.");
            }
            if (metrics.overdueTrackingAvailable() && !metrics.overdueTasks().isEmpty()) {
                risks.add("Overdue tasks require attention.");
            }

            List<String> bottlenecks = pendingTasks.stream()
                .filter(task -> "high".equalsIgnoreCase(task.getPriority()))
                .map(Task::getTitle)
                .filter(title -> title != null && !title.isBlank())
                .toList();
            List<String> priorities = pendingTasks.stream()
                .map(Task::getTitle)
                .filter(title -> title != null && !title.isBlank())
                .toList();
            List<String> recommendations = priorities.isEmpty()
                ? List.of("Review the project state and define the next deliverable.")
                : List.of("Prioritize and progress the pending tasks.");

            String request = "Analyze project risks, bottlenecks, priorities, and recommended next actions.";
            Map<String, Object> context = Map.of(
                "metrics", metrics,
                "tasks", tasks,
                "risks", risks,
                "bottlenecks", bottlenecks,
                "priorities", priorities,
                "recommendations", recommendations);
            LlmResponse aiAnalysis = llmService == null
                ? null
                : llmService.generateAnswer(request, List.of(), context);

            return new ProjectRiskAnalysis(metrics, risks, bottlenecks, priorities, recommendations, aiAnalysis);
            }

    private boolean isCompleted(Task task) {
        if (task.getStatus() == null) {
            return false;
        }
        String status = task.getStatus().toLowerCase(Locale.ROOT).trim();
        return status.equals("completed") || status.equals("done") || status.equals("closed");
    }

    private void requireValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }
}