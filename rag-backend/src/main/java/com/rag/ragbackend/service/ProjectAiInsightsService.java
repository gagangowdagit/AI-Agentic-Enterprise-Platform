package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.ProjectAiInsights;

public interface ProjectAiInsightsService {

    ProjectAiInsights getInsights(String projectId);
}