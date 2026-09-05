package com.rag.ragbackend.reporting;

public interface ReportingService {

    ProjectReport generateProjectReport(String projectId);

    AiProjectSummary generateAiProjectSummary(String projectId);
}