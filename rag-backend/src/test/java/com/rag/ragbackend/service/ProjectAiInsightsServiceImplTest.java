package com.rag.ragbackend.service;

import com.rag.ragbackend.agent.AIProjectManager;
import com.rag.ragbackend.analytics.ProjectAnalytics;
import com.rag.ragbackend.analytics.ProjectAnalyticsService;
import com.rag.ragbackend.analytics.ProjectRiskAnalysis;
import com.rag.ragbackend.dto.ProjectAiInsights;
import com.rag.ragbackend.entity.Project;
import com.rag.ragbackend.processing.LlmResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectAiInsightsServiceImplTest {

    @Test
    void combinesStructuredAnalyticsWithReadOnlyAiAnalysis() {
        ProjectAnalyticsService analyticsService = mock(ProjectAnalyticsService.class);
        AIProjectManager aiProjectManager = mock(AIProjectManager.class);
        ProjectAnalytics metrics = new ProjectAnalytics(
                new Project("project-1", "Alpha", "active"),
                4, 2, 2, 50.0, List.of(), false);
        ProjectRiskAnalysis riskAnalysis = new ProjectRiskAnalysis(
                metrics,
                List.of("Pending work remains."),
                List.of("Deployment is blocked."),
                List.of("Finish deployment"),
                List.of("Review the deployment plan."),
                null);
        when(analyticsService.analyzeRisks("project-1")).thenReturn(riskAnalysis);
        when(aiProjectManager.analyzeProject("project-1"))
                .thenReturn(new LlmResponse("Analyze project", "Project analysis.", List.of()));
        ProjectAiInsightsService service = new ProjectAiInsightsServiceImpl(analyticsService, aiProjectManager);

        ProjectAiInsights insights = service.getInsights("project-1");

        assertEquals(metrics, insights.metrics());
        assertEquals("NEEDS_ATTENTION", insights.health());
        assertEquals(List.of("Pending work remains."), insights.risks());
        assertEquals(List.of("Deployment is blocked."), insights.bottlenecks());
        assertEquals(List.of("Finish deployment"), insights.priorities());
        assertEquals(List.of("Review the deployment plan."), insights.recommendations());
        assertEquals("Project analysis.", insights.aiSummary());
        verify(analyticsService).analyzeRisks("project-1");
        verify(aiProjectManager).analyzeProject("project-1");
    }

    @Test
    void reportsHealthyForActiveProjectWithoutRisks() {
        ProjectAnalyticsService analyticsService = mock(ProjectAnalyticsService.class);
        AIProjectManager aiProjectManager = mock(AIProjectManager.class);
        ProjectAnalytics metrics = new ProjectAnalytics(
                new Project("project-1", "Alpha", "active"),
                0, 0, 0, 0.0, List.of(), false);
        when(analyticsService.analyzeRisks("project-1"))
                .thenReturn(new ProjectRiskAnalysis(metrics, List.of(), List.of(), List.of(), List.of(), null));
        when(aiProjectManager.analyzeProject("project-1")).thenReturn(null);
        ProjectAiInsightsService service = new ProjectAiInsightsServiceImpl(analyticsService, aiProjectManager);

        assertEquals("HEALTHY", service.getInsights("project-1").health());
    }
}