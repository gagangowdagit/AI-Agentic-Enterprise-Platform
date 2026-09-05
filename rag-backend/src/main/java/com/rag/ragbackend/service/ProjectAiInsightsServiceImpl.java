package com.rag.ragbackend.service;

import com.rag.ragbackend.agent.AIProjectManager;
import com.rag.ragbackend.analytics.ProjectAnalytics;
import com.rag.ragbackend.analytics.ProjectAnalyticsService;
import com.rag.ragbackend.analytics.ProjectRiskAnalysis;
import com.rag.ragbackend.dto.ProjectAiInsights;
import com.rag.ragbackend.processing.LlmResponse;
import org.springframework.stereotype.Service;

@Service
public class ProjectAiInsightsServiceImpl implements ProjectAiInsightsService {

    private final ProjectAnalyticsService projectAnalyticsService;
    private final AIProjectManager aiProjectManager;

    public ProjectAiInsightsServiceImpl(
            ProjectAnalyticsService projectAnalyticsService,
            AIProjectManager aiProjectManager) {
        this.projectAnalyticsService = projectAnalyticsService;
        this.aiProjectManager = aiProjectManager;
    }

    @Override
    public ProjectAiInsights getInsights(String projectId) {
        ProjectRiskAnalysis riskAnalysis = projectAnalyticsService.analyzeRisks(projectId);
        LlmResponse aiAnalysis = aiProjectManager.analyzeProject(projectId);
        ProjectAnalytics metrics = riskAnalysis.metrics();

        return new ProjectAiInsights(
                metrics,
                determineHealth(metrics, riskAnalysis),
                riskAnalysis.risks(),
                riskAnalysis.bottlenecks(),
                riskAnalysis.priorities(),
                riskAnalysis.recommendations(),
                aiAnalysis == null ? null : aiAnalysis.answer());
    }

    private String determineHealth(ProjectAnalytics metrics, ProjectRiskAnalysis riskAnalysis) {
        if (metrics.project().getStatus() == null
                || !metrics.project().getStatus().equalsIgnoreCase("active")
                || !riskAnalysis.risks().isEmpty()) {
            return "NEEDS_ATTENTION";
        }
        return "HEALTHY";
    }
}