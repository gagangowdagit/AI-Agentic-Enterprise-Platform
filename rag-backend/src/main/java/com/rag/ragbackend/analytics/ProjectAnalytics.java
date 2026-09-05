package com.rag.ragbackend.analytics;

import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.entity.Task;

import java.util.List;

public record ProjectAnalytics(
        Project project,
        int totalTasks,
        int completedTasks,
        int pendingTasks,
        double completionPercentage,
        List<Task> overdueTasks,
        boolean overdueTrackingAvailable) {
}