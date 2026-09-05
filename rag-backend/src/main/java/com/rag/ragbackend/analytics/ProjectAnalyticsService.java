package com.rag.ragbackend.analytics;

public interface ProjectAnalyticsService {

    ProjectAnalytics analyzeProject(String projectId);

    ProjectRiskAnalysis analyzeRisks(String projectId);
}