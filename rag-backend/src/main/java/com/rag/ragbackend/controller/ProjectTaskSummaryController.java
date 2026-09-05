package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.ApiResponse;
import com.rag.ragbackend.dto.ProjectTaskSummary;
import com.rag.ragbackend.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectTaskSummaryController {

    private final TaskService taskService;

    public ProjectTaskSummaryController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{projectId}/task-summary")
    public ResponseEntity<ApiResponse<ProjectTaskSummary>> getTaskSummary(@PathVariable String projectId) {
        return ResponseEntity.ok(ApiResponse.success(
                taskService.getTaskSummaryByProjectId(projectId),
                "Project task summary retrieved."));
    }
}