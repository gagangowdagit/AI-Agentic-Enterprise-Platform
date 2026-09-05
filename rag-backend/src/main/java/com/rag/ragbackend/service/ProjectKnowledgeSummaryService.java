package com.rag.ragbackend.service;

import com.rag.ragbackend.dto.ProjectKnowledgeSummary;

public interface ProjectKnowledgeSummaryService {

    ProjectKnowledgeSummary getSummary(String projectId);
}