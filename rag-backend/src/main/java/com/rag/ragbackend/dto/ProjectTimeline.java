package com.rag.ragbackend.dto;

import java.time.LocalDate;
import java.util.List;

public record ProjectTimeline(
        String projectId,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        Long daysRemaining,
        List<ProjectTimelineTask> tasks) {
}