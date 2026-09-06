package com.rag.ragbackend.controller;

import com.rag.ragbackend.dto.CompanyAnalyticsResponse;
import com.rag.ragbackend.service.CompanyAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}