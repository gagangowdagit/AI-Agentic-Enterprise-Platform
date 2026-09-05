package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.ApiResponse;
import com.rag.ragbackend.dto.ProjectAiInsights;
import com.rag.ragbackend.service.ProjectAiInsightsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectAiInsightsController {

    private final ProjectAiInsightsService insightsService;

    public ProjectAiInsightsController(ProjectAiInsightsService insightsService) {
        this.insightsService = insightsService;
    }

    @GetMapping("/{projectId}/ai-insights")
    public ResponseEntity<ApiResponse<ProjectAiInsights>> getInsights(@PathVariable String projectId) {
        return ResponseEntity.ok(ApiResponse.success(
                insightsService.getInsights(projectId),
                "Project AI insights generated."));
    }
}