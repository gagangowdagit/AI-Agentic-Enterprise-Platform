package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.ApiResponse;
import com.rag.ragbackend.dto.ProjectTimeline;
import com.rag.ragbackend.service.ProjectTimelineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectTimelineController {

    private final ProjectTimelineService timelineService;

    public ProjectTimelineController(ProjectTimelineService timelineService) {
        this.timelineService = timelineService;
    }

    @GetMapping("/{projectId}/timeline")
    public ResponseEntity<ApiResponse<ProjectTimeline>> getTimeline(@PathVariable String projectId) {
        return ResponseEntity.ok(ApiResponse.success(
                timelineService.getTimeline(projectId),
                "Project timeline retrieved."));
    }
}