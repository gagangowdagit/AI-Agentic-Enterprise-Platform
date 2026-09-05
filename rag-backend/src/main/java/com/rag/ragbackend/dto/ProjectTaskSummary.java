package com.rag.ragbackend.dto;

import com.rag.ragbackend.entity.Task;

import java.util.List;

public record ProjectTaskSummary(
        int totalTasks,
        int completedTasks,
        int inProgressTasks,
        int pendingTasks,
        List<Task> overdueTasks,
        double completionPercentage,
        boolean overdueTrackingAvailable) {
}