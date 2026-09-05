package com.rag.ragbackend.dto;

import com.rag.ragbackend.analytics.ProjectAnalytics;

import java.util.List;

public record ProjectAiInsights(
        ProjectAnalytics metrics,
        String health,
        List<String> risks,
        List<String> bottlenecks,
        List<String> priorities,
        List<String> recommendations,
        String aiSummary) {
}