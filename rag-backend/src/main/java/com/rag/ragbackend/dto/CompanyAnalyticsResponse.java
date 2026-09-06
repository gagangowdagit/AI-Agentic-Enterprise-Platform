package com.rag.ragbackend.dto;

public record CompanyAnalyticsResponse(
        long totalProjects,
        long totalDepartments,
        long totalEmployees,
        long completedProjects,
        long inProgressProjects,
        long pendingProjects) {
}