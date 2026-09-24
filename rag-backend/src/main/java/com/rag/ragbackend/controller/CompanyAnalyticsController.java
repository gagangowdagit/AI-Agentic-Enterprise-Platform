package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.CompanyAnalyticsResponse;
import com.rag.ragbackend.dto.DepartmentAnalyticsResponse;
import com.rag.ragbackend.dto.EmployeeAnalyticsResponse;
import com.rag.ragbackend.service.CompanyAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
public class CompanyAnalyticsController {

    private final CompanyAnalyticsService analyticsService;

    public CompanyAnalyticsController(CompanyAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    public CompanyAnalyticsResponse getOverview() {
        return analyticsService.getOverview();
    }

    @GetMapping("/departments")
    public List<DepartmentAnalyticsResponse> getDepartmentBreakdown() {
        return analyticsService.getDepartmentBreakdown();
    }

    @GetMapping("/employees")
    public List<EmployeeAnalyticsResponse> getEmployeeBreakdown() {
        return analyticsService.getEmployeeBreakdown();
    }
}