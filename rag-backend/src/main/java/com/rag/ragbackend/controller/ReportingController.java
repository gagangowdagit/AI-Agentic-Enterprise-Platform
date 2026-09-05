package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.ApiResponse;
import com.rag.ragbackend.reporting.AiProjectSummary;
import com.rag.ragbackend.reporting.ReportingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports/projects")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<AiProjectSummary>> getProjectReport(@PathVariable String projectId) {
        AiProjectSummary report = reportingService.generateAiProjectSummary(projectId);
        return ResponseEntity.ok(ApiResponse.success(report, "Project report generated."));
    }
}