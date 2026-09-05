package com.rag.ragbackend.reporting;

import com.rag.ragbackend.entity.Task;

import java.util.List;

public record ProjectReport(
        String projectId,
        String projectName,
        String projectStatus,
        int totalTasks,
        int completedTasks,
        int pendingTasks,
        double completionPercentage,
        List<Task> overdueTasks,
        boolean overdueTrackingAvailable) {
}