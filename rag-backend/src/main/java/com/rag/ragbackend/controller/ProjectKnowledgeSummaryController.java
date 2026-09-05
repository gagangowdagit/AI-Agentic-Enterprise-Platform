package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.ApiResponse;
import com.rag.ragbackend.dto.ProjectKnowledgeSummary;
import com.rag.ragbackend.service.ProjectKnowledgeSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectKnowledgeSummaryController {

    private final ProjectKnowledgeSummaryService knowledgeSummaryService;

    public ProjectKnowledgeSummaryController(ProjectKnowledgeSummaryService knowledgeSummaryService) {
        this.knowledgeSummaryService = knowledgeSummaryService;
    }

    @GetMapping("/{projectId}/knowledge-summary")
    public ResponseEntity<ApiResponse<ProjectKnowledgeSummary>> getKnowledgeSummary(
            @PathVariable String projectId) {
        return ResponseEntity.ok(ApiResponse.success(
                knowledgeSummaryService.getSummary(projectId),
                "Project knowledge summary retrieved."));
    }
}