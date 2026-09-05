package com.rag.ragbackend.controller;

import com.rag.ragbackend.agent.AIProjectManager;
import com.rag.ragbackend.dto.AIProjectQueryRequest;
import com.rag.ragbackend.dto.ApiResponse;
import com.rag.ragbackend.processing.LlmResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai/projects")
public class AIProjectManagerController {

    private final AIProjectManager projectManager;

    public AIProjectManagerController(AIProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    @PostMapping("/{projectId}/query")
    public ResponseEntity<ApiResponse<LlmResponse>> queryProject(
            @PathVariable String projectId,
            @Valid @RequestBody AIProjectQueryRequest request) {
        LlmResponse response = projectManager.manageProject(projectId, request.getRequest());
        return ResponseEntity.ok(ApiResponse.success(response, "Project request processed."));
    }
}