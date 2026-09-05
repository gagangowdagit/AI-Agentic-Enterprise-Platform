package com.rag.ragbackend.reporting;

import com.rag.ragbackend.analytics.ProjectAnalytics;
import com.rag.ragbackend.analytics.ProjectAnalyticsService;
import com.rag.ragbackend.entity.Task;
import com.rag.ragbackend.processing.LlmResponse;
import com.rag.ragbackend.processing.LlmService;
import com.rag.ragbackend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportingServiceImpl implements ReportingService {

    private final ProjectAnalyticsService projectAnalyticsService;
    private final TaskService taskService;
    private final LlmService llmService;

    public ReportingServiceImpl(ProjectAnalyticsService projectAnalyticsService) {
        this(projectAnalyticsService, null, null);
    }

    @Autowired
    public ReportingServiceImpl(
            ProjectAnalyticsService projectAnalyticsService,
            TaskService taskService,
            LlmService llmService) {
        this.projectAnalyticsService = projectAnalyticsService;
        this.taskService = taskService;
        this.llmService = llmService;
    }

    @Override
    public ProjectReport generateProjectReport(String projectId) {
        ProjectAnalytics analytics = projectAnalyticsService.analyzeProject(projectId);
        return new ProjectReport(
                analytics.project().getId(),
                analytics.project().getName(),
                analytics.project().getStatus(),
                analytics.totalTasks(),
                analytics.completedTasks(),
                analytics.pendingTasks(),
                analytics.completionPercentage(),
                analytics.overdueTasks(),
                analytics.overdueTrackingAvailable());
    }

    @Override
    public AiProjectSummary generateAiProjectSummary(String projectId) {
        if (taskService == null || llmService == null) {
            throw new IllegalStateException("TaskService and LlmService are required for an AI project summary.");
        }

        ProjectReport report = generateProjectReport(projectId);
        List<Task> tasks = taskService.getTasksByProjectId(projectId);
        Map<String, Object> context = new HashMap<>();
        context.put("project", report.projectId());
        context.put("projectName", report.projectName());
        context.put("projectStatus", report.projectStatus());
        context.put("metrics", report);
        context.put("tasks", tasks);
        context.put("overdueTasks", report.overdueTasks());
        context.put("overdueTrackingAvailable", report.overdueTrackingAvailable());

        String request = "Provide a concise project report covering project health, progress, key issues, and areas needing attention.";
        LlmResponse response = llmService.generateAnswer(request, List.of(), context);
        return new AiProjectSummary(report, response.answer());
    }
}