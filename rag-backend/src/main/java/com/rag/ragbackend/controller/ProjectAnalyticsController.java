package com.rag.ragbackend.controller;

import com.rag.ragbackend.analytics.ProjectAnalyticsService;
import com.rag.ragbackend.analytics.ProjectRiskAnalysis;
import com.rag.ragbackend.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics/projects")
public class ProjectAnalyticsController {

    private final ProjectAnalyticsService analyticsService;

    public ProjectAnalyticsController(ProjectAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectRiskAnalysis>> analyzeProject(@PathVariable String projectId) {
        ProjectRiskAnalysis analysis = analyticsService.analyzeRisks(projectId);
        return ResponseEntity.ok(ApiResponse.success(analysis, "Project analytics generated."));
    }
}